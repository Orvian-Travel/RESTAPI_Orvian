package com.orvian.travelapi.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import static java.util.Optional.ofNullable;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.orvian.travelapi.controller.dto.reservation.CreateReservationDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationSearchResultDTO;
import com.orvian.travelapi.domain.enums.ReservationSituation;
import com.orvian.travelapi.domain.model.PackageDate;
import com.orvian.travelapi.domain.model.Payment;
import com.orvian.travelapi.domain.model.Reservation;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.PackageDateRepository;
import com.orvian.travelapi.domain.repository.PaymentRepository;
import com.orvian.travelapi.domain.repository.ReservationRepository;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.mapper.PaymentMapper;
import com.orvian.travelapi.mapper.ReservationMapper;
import com.orvian.travelapi.service.ReservationService;
import com.orvian.travelapi.service.exception.BusinessException;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;

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

    @Override
    public List<ReservationSearchResultDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(reservation -> {
                    Payment payment = paymentRepository.findByReservation_Id(reservation.getId()).orElse(null);
                    return reservationMapper.toDTO(reservation, payment);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Reservation create(Record dto) {

        try {

            CreateReservationDTO dtoReservation = (CreateReservationDTO) dto;

            User user = userRepository.findById(dtoReservation.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + dtoReservation.userId()));
            PackageDate packageDate = packageDateRepository.findById(dtoReservation.packageDateId())
                    .orElseThrow(() -> new IllegalArgumentException("Package date not found with ID: " + dtoReservation.packageDateId()));

            if (reservationRepository.existsByUserIdAndPackageDateId(dtoReservation.userId(), dtoReservation.packageDateId())) {
                throw new DuplicatedRegistryException("A reservation already exists for this user and package date");
            }

            Reservation reservation = reservationMapper.toEntity((CreateReservationDTO) dto);
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

            if (dtoReservation.payment() != null) {
                Payment payment = paymentMapper.toEntity(dtoReservation.payment());
                payment.setReservation(reservation); // associa a reserva ao pagamento
                paymentRepository.save(payment);
                paymentRepository.flush();
            }

            return savedReservation;
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for reservation creation: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid argument provided for reservation creation: " + e.getMessage());
        } catch (RuntimeException e) {
            handlePersistenceError(e);
            return null;
        }

    }

    @Override
    public ReservationSearchResultDTO findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));

        // Busca o pagamento relacionado
        Payment payment = paymentRepository.findByReservation_Id(reservation.getId()).orElse(null);

        // Monta o DTO passando o pagamento
        return reservationMapper.toDTO(reservation, payment);
    }

    @Override
    public void update(UUID id, Record dto) {

    }

    public void updateCancel(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        if (reservation.getReservationDate().minusDays(1).isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot update reservation less than 1 day before the reservation date");
        }

        reservation.setSituation(ReservationSituation.cancelada);
        reservation.setCancelledDate(LocalDate.now());
        reservation.setUpdateAt(LocalDateTime.now());
        reservationRepository.save(reservation);
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        reservationRepository.findById(id)
                .map(reservation -> {
                    reservationRepository.delete(reservation);
                    return reservation;
                })
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
    }

    private void handlePersistenceError(Throwable e) {
        Throwable rootCause = e;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        String message = (rootCause.getMessage() != null) ? rootCause.getMessage() : e.getMessage();
        log.error("Persistence error: {}", message);
        throw new BusinessException("Falha ao processar reserva: " + message);
    }
}
