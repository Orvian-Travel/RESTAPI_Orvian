package com.orvian.travelapi.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.orvian.travelapi.controller.dto.reservation.CreateReservationDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationDateDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationSearchResultDTO;
import com.orvian.travelapi.controller.dto.reservation.UpdateReservationDTO;
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
                Payment payment = paymentMapper.toEntity(dtoReservation.payment());
                payment.setReservation(savedReservation); // associa a reserva ao pagamento
                paymentRepository.save(payment);
                paymentRepository.flush();
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

}
