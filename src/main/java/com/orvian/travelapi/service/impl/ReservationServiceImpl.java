package com.orvian.travelapi.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.orvian.travelapi.controller.dto.admin.ReservationToSheetDTO;
import com.orvian.travelapi.controller.dto.payment.CreatePaymentDTO;
import com.orvian.travelapi.controller.dto.reservation.CreateReservationDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationDateDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationSearchResultDTO;
import com.orvian.travelapi.controller.dto.reservation.UpdateReservationDTO;
import com.orvian.travelapi.domain.enums.PaymentStatus;
import com.orvian.travelapi.domain.enums.ReservationSituation;
import com.orvian.travelapi.domain.model.Media;
import com.orvian.travelapi.domain.model.PackageDate;
import com.orvian.travelapi.domain.model.Payment;
import com.orvian.travelapi.domain.model.Reservation;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.MediaRepository;
import com.orvian.travelapi.domain.repository.PackageDateRepository;
import com.orvian.travelapi.domain.repository.PaymentRepository;
import com.orvian.travelapi.domain.repository.ReservationRepository;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.mapper.PaymentMapper;
import com.orvian.travelapi.mapper.ReservationMapper;
import com.orvian.travelapi.service.PaymentService;
import com.orvian.travelapi.service.ReservationService;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import com.orvian.travelapi.service.exception.NotFoundException;
import static com.orvian.travelapi.service.exception.PersistenceExceptionUtil.handlePersistenceError;
import com.orvian.travelapi.specs.ReservationSpecs;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PackageDateRepository packageDateRepository;
    private final ReservationMapper reservationMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final MediaRepository mediaRepository;
    private final PaymentService paymentService;

    @Override
    public Page<ReservationSearchResultDTO> findAll(Integer pageNumber, Integer pageSize, UUID userID) {
        try {
            log.info("Retrieving all reservations for user ID: {}", userID);

            Specification<Reservation> spec = (userID != null) ? ReservationSpecs.userIdEquals(userID) : null;
            Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());

            return reservationRepository
                    .findAll(spec, pageRequest)
                    .map(reservation -> {
                        Payment payment = paymentRepository.findByReservation_Id(reservation.getId()).orElse(null);

                        // ✅ NOVA LÓGICA: Buscar primeira mídia do pacote associado
                        Optional<Media> firstMedia = mediaRepository.findFirstByTravelPackage_IdOrderByCreatedAtAsc(
                                reservation.getPackageDate().getTravelPackage().getId()
                        );

                        return reservationMapper.toDTOWithFirstMedia(reservation, payment, firstMedia.orElse(null));
                    });
        } catch (Exception e) {
            log.error("Erro ao buscar reservas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar reservas: " + e.getMessage());
        }
    }

    @Override
    public Page<ReservationSearchResultDTO> findAllByStatusAndDate(Integer pageNumber, Integer pageSize,
            UUID userId, ReservationSituation status, LocalDate reservationDate) {
        try {
            log.info("Retrieving reservations for user ID: {} with status: {} and reservationDate: {}",
                    userId, status, reservationDate);

            Specification<Reservation> spec = ReservationSpecs.userIdAndSituationAndReservationDate(
                    userId, status, reservationDate);

            Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());

            return reservationRepository
                    .findAll(spec, pageRequest)
                    .map(reservation -> {
                        Payment payment = paymentRepository.findByReservation_Id(reservation.getId()).orElse(null);

                        // ✅ NOVA LÓGICA: Buscar primeira mídia do pacote associado
                        Optional<Media> firstMedia = mediaRepository.findFirstByTravelPackage_IdOrderByCreatedAtAsc(
                                reservation.getPackageDate().getTravelPackage().getId()
                        );

                        return reservationMapper.toDTOWithFirstMedia(reservation, payment, firstMedia.orElse(null));
                    });
        } catch (Exception e) {
            log.error("Erro ao buscar reservas por status e data: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar reservas por status e data: " + e.getMessage());
        }
    }

    @Override
    public Reservation create(Record dto) {

        try {

            CreateReservationDTO dtoReservation = (CreateReservationDTO) dto;

            User user = userRepository.findById(dtoReservation.userId())
                    .orElseThrow(() -> new NotFoundException("User not found with ID: " + dtoReservation.userId()));
            PackageDate packageDate = packageDateRepository.findById(dtoReservation.packageDateId())
                    .orElseThrow(() -> new NotFoundException("Package date not found with ID: " + dtoReservation.packageDateId()));

            if (reservationRepository.existsByUserIdAndPackageDateId(dtoReservation.userId(), dtoReservation.packageDateId())) {
                throw new DuplicatedRegistryException("A reservation already exists for this user and package date");
            }

            Reservation reservation = reservationMapper.toEntity(dtoReservation);
            log.info("Creating reservation with ID: {}", reservation);

            reservation.setUser(user);
            reservation.setPackageDate(packageDate);
            reservation.setTravelers(ofNullable(dtoReservation.travelers())
                    .orElse(List.of())
                    .stream()
                    .filter(Objects::nonNull)
                    .map(reservationMapper::toEntity)
                    .collect(Collectors.toList()));

            Reservation savedReservation = reservationRepository.save(reservation);
            reservationRepository.flush();

            if (dtoReservation.payment() != null) {
                processReservationPayment(dtoReservation.payment(), savedReservation);
            }

            return savedReservation;
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for reservation creation: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid argument provided for reservation creation: " + e.getMessage());
        } catch (RuntimeException e) {
            handlePersistenceError(e, log);
            return null;
        }

    }

    @Override
    public ReservationSearchResultDTO findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found with ID: " + id));

        Payment payment = paymentRepository.findByReservation_Id(reservation.getId()).orElse(null);

        return reservationMapper.toDTO(reservation, payment);
    }

    @Override
    public void update(UUID id, Record dto) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);
        if (reservationOptional.isEmpty()) {
            log.error("Reservation with id {} not found", id);
            throw new NotFoundException("Reservation with id " + id + " not found.");
        }
        try {
            Reservation reservation = reservationOptional.get();
            log.info("Updating reservation with ID: {}", reservation.getId());

            reservationMapper.updateEntityFromDTO((UpdateReservationDTO) dto, reservation);
            Reservation updatedReservation = reservationRepository.save(reservation);
            log.info("Reservation updated with ID: {}", updatedReservation.getId());
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for payment update: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid argument provided for payment update: " + e.getMessage());

        } catch (RuntimeException e) {
            handlePersistenceError(e, log);
        }
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        try {
            Reservation reservation = reservationRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Reservation not found with ID: " + id));

            if (ReservationSituation.CANCELADA.equals(reservation.getSituation())) {
                log.warn("Reservation {} is already cancelled", id);
                throw new IllegalStateException("Reservation is already cancelled");
            }

            reservation.setSituation(ReservationSituation.CANCELADA);

            Reservation cancelledReservation = reservationRepository.save(reservation);

            log.info("Reservation {} successfully cancelled (soft delete). Status changed to CANCELADA",
                    cancelledReservation.getId());

        } catch (NotFoundException | IllegalStateException e) {
            // Re-throw exceptions específicas
            throw e;
        } catch (Exception e) {
            log.error("Error cancelling reservation {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error cancelling reservation: " + e.getMessage());
        }
    }

    @Override
    public List<ReservationDateDTO> findAvailableReservationDates(UUID userId) {
        try {
            log.info("Finding available reservation dates for user ID: {}", userId);

            List<LocalDate> results;

            if (userId != null) {
                // Para usuário específico
                results = reservationRepository.findDistinctReservationDatesByUserId(userId);
            } else {
                // Para ADMIN - todas as datas do sistema
                results = reservationRepository.findAllDistinctReservationDates();
            }

            List<ReservationDateDTO> dates = results.stream()
                    .map(ReservationDateDTO::new)
                    .toList();

            log.info("Found {} distinct reservation dates for user {}", dates.size(), userId);
            return dates;

        } catch (Exception e) {
            log.error("Error finding available reservation dates for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error finding available reservation dates: " + e.getMessage());
        }
    }

    public boolean isReservationOwnedByUser(UUID reservationId, UUID userId) {
        try {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new NotFoundException("Reservation not found with id: " + reservationId));

            UUID reservationOwnerId = reservation.getUser().getId();
            boolean isOwner = reservationOwnerId.equals(userId);

            log.debug("Reservation ownership check: reservationId={}, userId={}, isOwner={}",
                    reservationId, userId, isOwner);

            return isOwner;
        } catch (Exception e) {
            log.error("Error checking reservation ownership: {}", e.getMessage());
            return false;
        }
    }

    private void processReservationPayment(CreatePaymentDTO paymentDTO, Reservation savedReservation) {
        try {
            log.info("Processing payment for reservation: {}", savedReservation.getId());

            Payment payment = paymentMapper.toEntity(paymentDTO);
            payment.setReservation(savedReservation);

            if (PaymentStatus.APROVADO.equals(payment.getStatus())) {
                payment.setPaymentApprovedAt(new Date());
                log.info("Payment approved, setting paymentApprovedAt for reservation: {}", savedReservation.getId());
            }

            Payment savedPayment = paymentRepository.save(payment);
            paymentRepository.flush();

            if (PaymentStatus.APROVADO.equals(savedPayment.getStatus())) {
                log.info("Sending payment confirmation email for reservation: {}", savedReservation.getId());
                paymentService.sendPaymentConfirmationEmailPublic(savedPayment);
            }

            log.info("Payment processed successfully for reservation: {}", savedReservation.getId());

        } catch (Exception e) {
            log.error("Error processing payment for reservation {}: {}", savedReservation.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to process payment for reservation: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportReservationsToExcel() {
        try {
            List<ReservationToSheetDTO> data = reservationRepository.exportToSheet();

            if (data.isEmpty()) {
                log.warn("No reservation data found for export");
                return new byte[0];
            }

            try (var workbook = new XSSFWorkbook()) {
                var sheet = workbook.createSheet("Reservas");

                createExcelHeader(workbook, sheet);
                populateExcelData(sheet, data);
                autoSizeExcelColumns(sheet);

                try (var out = new ByteArrayOutputStream()) {
                    workbook.write(out);
                    log.info("Successfully generated Excel file with {} reservations", data.size());
                    return out.toByteArray();
                }
            }
        } catch (Exception e) {
            log.error("Error generating Excel export: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate Excel export", e);
        }
    }

    @Override
    public byte[] exportReservationsToPdf() {
        try {
            List<ReservationToSheetDTO> data = reservationRepository.exportToSheet();

            if (data.isEmpty()) {
                log.warn("No reservation data found for PDF export");
                return new byte[0];
            }

            try (var out = new ByteArrayOutputStream()) {
                var pdfWriter = new PdfWriter(out);
                var pdfDocument = new PdfDocument(pdfWriter);

                // ✅ CONFIGURAR ORIENTAÇÃO HORIZONTAL (PAISAGEM)
                pdfDocument.setDefaultPageSize(PageSize.A4.rotate());

                var document = new Document(pdfDocument);

                // ✅ CONFIGURAR MARGENS MENORES PARA APROVEITAR MELHOR O ESPAÇO
                document.setMargins(20, 20, 20, 20);

                // ✅ CONFIGURAR FONTE PARA PORTUGUÊS
                var font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

                // ✅ TÍTULO DO DOCUMENTO
                var title = new Paragraph("Relatório de Reservas - Orvian Travel")
                        .setFont(font)
                        .setFontSize(16) // ✅ REDUZIDO PARA CABER MELHOR
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(15);
                document.add(title);

                // ✅ DATA DE GERAÇÃO
                var dateGenerated = new Paragraph(
                        "Gerado em: " + java.time.LocalDateTime.now()
                                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                        .setFont(font)
                        .setFontSize(9)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMarginBottom(15);
                document.add(dateGenerated);

                // ✅ CRIAR TABELA COM LARGURAS OTIMIZADAS PARA HORIZONTAL
                var table = new Table(UnitValue.createPercentArray(new float[]{
                    12f, // ID Reserva
                    12f, // ID Pacote  
                    8f, // Preço
                    20f, // Título (mais espaço)
                    10f, // Data Reserva
                    8f, // Situação
                    10f, // Data Cancelamento
                    15f, // Email (mais espaço)
                    5f // Viajantes
                })).useAllAvailableWidth();

                // ✅ HEADER DA TABELA
                addPdfTableHeader(table, font);

                // ✅ DADOS DA TABELA
                addPdfTableData(table, data, font);

                document.add(table);

                // ✅ RODAPÉ
                var footer = new Paragraph(
                        String.format("Total de registros: %d | © 2025 Orvian Travel", data.size()))
                        .setFont(font)
                        .setFontSize(8)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(15);
                document.add(footer);

                document.close();

                log.info("Successfully generated horizontal PDF file with {} reservations", data.size());
                return out.toByteArray();
            }
        } catch (Exception e) {
            log.error("Error generating PDF export: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF export", e);
        }
    }

    private void createExcelHeader(XSSFWorkbook workbook,
            XSSFSheet sheet) {
        var headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);

        var font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);

        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        var header = sheet.createRow(0);
        header.setHeight((short) 500);

        String[] headers = {
            "ID da Reserva", "ID do Pacote", "Preço", "Título do Pacote",
            "Data da Reserva", "Situação", "Data de Cancelamento",
            "Email do Usuário", "Quantidade de Viajantes"
        };

        for (int i = 0; i < headers.length; i++) {
            var cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void populateExcelData(XSSFSheet sheet,
            List<ReservationToSheetDTO> data) {

        var workbook = sheet.getWorkbook();
        var dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);

        var currencyStyle = workbook.createCellStyle();
        currencyStyle.cloneStyleFrom(dataStyle);
        var currencyFormat = workbook.createDataFormat();
        currencyStyle.setDataFormat(currencyFormat.getFormat("R$ #,##0.00"));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        int rowIdx = 1;
        for (ReservationToSheetDTO dto : data) {
            var row = sheet.createRow(rowIdx++);

            var cell0 = row.createCell(0);
            cell0.setCellValue(dto.reservationId() != null ? dto.reservationId() : "");
            cell0.setCellStyle(dataStyle);

            var cell1 = row.createCell(1);
            cell1.setCellValue(dto.packageId() != null ? dto.packageId() : "");
            cell1.setCellStyle(dataStyle);

            var cell2 = row.createCell(2);
            cell2.setCellValue(dto.price() != null ? dto.price().doubleValue() : 0.0);
            cell2.setCellStyle(currencyStyle); // ✅ FORMATO MOEDA

            var cell3 = row.createCell(3);
            cell3.setCellValue(dto.packageTitle() != null ? dto.packageTitle() : "");
            cell3.setCellStyle(dataStyle);

            var cell4 = row.createCell(4);
            if (dto.reservationDate() != null) {
                LocalDate localDate = dto.reservationDate().toLocalDate();
                cell4.setCellValue(localDate.format(dateFormatter));
            } else {
                cell4.setCellValue("");
            }
            cell4.setCellStyle(dataStyle);

            var cell5 = row.createCell(5);
            cell5.setCellValue(dto.situation() != null ? dto.situation() : "");
            cell5.setCellStyle(dataStyle);

            var cell6 = row.createCell(6);
            if (dto.cancelDate() != null) {
                LocalDate localDate = dto.cancelDate().toLocalDate();
                cell6.setCellValue(localDate.format(dateFormatter));
            } else {
                cell6.setCellValue("");
            }
            cell6.setCellStyle(dataStyle);

            var cell7 = row.createCell(7);
            cell7.setCellValue(dto.userEmail() != null ? dto.userEmail() : "");
            cell7.setCellStyle(dataStyle);

            var cell8 = row.createCell(8);

            cell8.setCellValue(Objects.requireNonNullElse(dto.qtdViajantes(), 0));
            cell8.setCellStyle(dataStyle);
        }

    }

    private void autoSizeExcelColumns(XSSFSheet sheet) {
        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);

            int currentWidth = sheet.getColumnWidth(i);
            if (currentWidth > 6000) {
                sheet.setColumnWidth(i, 6000);
            }
        }
    }

    private void addPdfTableHeader(Table table,
            PdfFont font) {
        String[] headers = {
            "ID Reserva", "ID Pacote", "Preço", "Título",
            "Data Reserva", "Situação", "Data Cancel.", "Email", "Viajantes"
        };

        for (String header : headers) {
            var headerCell = new Cell()
                    .add(new Paragraph(header))
                    .setFont(font)
                    .setFontSize(9)
                    .setBold()
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(5);
            table.addHeaderCell(headerCell);
        }
    }

    private void addPdfTableData(Table table,
            List<ReservationToSheetDTO> data,
            PdfFont font) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (ReservationToSheetDTO dto : data) {
            // ID Reserva (primeiros 8 chars)
            table.addCell(createPdfCell(
                    dto.reservationId() != null ? dto.reservationId().substring(0, 8) + "..." : "",
                    font, 8));

            // ID Pacote (primeiros 8 chars)
            table.addCell(createPdfCell(
                    dto.packageId() != null ? dto.packageId().substring(0, 8) + "..." : "",
                    font, 8));

            // Preço
            table.addCell(createPdfCell(
                    dto.price() != null ? String.format("R$ %.2f", dto.price().doubleValue()) : "R$ 0,00",
                    font, 8));

            // Título (limitado)
            String title = dto.packageTitle() != null ? dto.packageTitle() : "";
            if (title.length() > 25) {
                title = title.substring(0, 25) + "...";
            }
            table.addCell(createPdfCell(title, font, 8));

            // Data Reserva
            String formattedReservationDate = "";
            if (dto.reservationDate() != null) {
                LocalDate localDate = dto.reservationDate().toLocalDate();
                formattedReservationDate = dateFormatter.format(localDate);
            }
            table.addCell(createPdfCell(formattedReservationDate, font, 8));

            // Situação
            table.addCell(createPdfCell(
                    dto.situation() != null ? dto.situation() : "",
                    font, 8));

            // Data Cancelamento
            String formattedCancelDate = "";
            if (dto.cancelDate() != null) {
                LocalDate localDate = dto.cancelDate().toLocalDate();
                formattedCancelDate = dateFormatter.format(localDate);
            }
            table.addCell(createPdfCell(formattedCancelDate, font, 8));

            // Email (limitado)
            String email = dto.userEmail() != null ? dto.userEmail() : "";
            if (email.length() > 20) {
                email = email.substring(0, 20) + "...";
            }
            table.addCell(createPdfCell(email, font, 7));

            // Viajantes
            table.addCell(createPdfCell(
                    String.valueOf(Objects.requireNonNullElse(dto.qtdViajantes(), 0)),
                    font, 8));
        }
    }

    private Cell createPdfCell(String content,
            PdfFont font,
            int fontSize) {
        return new Cell()
                .add(new Paragraph(content))
                .setFont(font)
                .setFontSize(fontSize)
                .setPadding(3)
                .setTextAlignment(TextAlignment.CENTER);
    }
}
