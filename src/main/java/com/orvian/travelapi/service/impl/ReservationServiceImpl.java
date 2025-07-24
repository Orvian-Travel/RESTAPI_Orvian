package com.orvian.travelapi.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import static java.util.Optional.ofNullable;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.orvian.travelapi.controller.dto.reservation.CreateReservationDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationSearchResultDTO;
import com.orvian.travelapi.domain.enums.ReservationSituation;
import com.orvian.travelapi.domain.model.PackageDate;
import com.orvian.travelapi.domain.model.Payment;
import com.orvian.travelapi.domain.model.Reservation;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.PackageDateRepository;
import com.orvian.travelapi.domain.repository.ReservationRepository;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.mapper.ReservationMapper;
import com.orvian.travelapi.service.ReservationService;
import com.orvian.travelapi.service.exception.BusinessException;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PackageDateRepository packageDateRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public List<ReservationSearchResultDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDTO)
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
            boolean exists = reservationRepository.existsByUserIdAndPackageDateId(dtoReservation.userId(), dtoReservation.packageDateId());

            if (exists) {
                throw new DuplicatedRegistryException("A reservation already exists for this user and package date");
            }

            Reservation reservation = reservationMapper.toEntity((CreateReservationDTO) dto);
            log.info("Creating reservation with ID: {}", reservation.getId());

            reservation.setUser(user);
            reservation.setPackageDate(packageDate);
            reservation.setTravelers(ofNullable(dtoReservation.travelers())
                    .orElse(List.of())
                    .stream()
                    .filter(Objects::nonNull)
                    .map(reservationMapper::toEntity)
                    .collect(Collectors.toList()));

            if (dtoReservation.payment() != null) {
                Payment payment = reservationMapper.toEntity(dtoReservation.payment());
                reservation.setPayment(payment);
            }

            Reservation savedReservation = reservationRepository.save(reservation);
            log.info("Reservation created with ID: {}", savedReservation.getId());

            return savedReservation;
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for reservation creation: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid argument provided for reservation creation: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            handleErrorSQL(e);
            return null; // This line will not be reached, but is needed to satisfy the compiler
        }
    }

    // public Reservation create(CreateReservationDTO dto) {
    //     User user = userRepository.findById(dto.userId())
    //             .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + dto.userId()));
    //     PackageDate packageDate = packageDateRepository.findById(dto.packageDateId())
    //             .orElseThrow(() -> new IllegalArgumentException("Package date not found with ID: " + dto.packageDateId()));
    //     boolean exists = reservationRepository.existsByUserIdAndPackageDateId(dto.userId(), dto.packageDateId());
    //     if (exists) {
    //         throw new IllegalArgumentException("A reservation already exists for this user and package date");
    //     }
    //     List<Traveler> travelers = dto.travelers().stream()
    //             .map(travelerDTO -> {
    //                 Traveler traveler = new Traveler();
    //                 traveler.setName(travelerDTO.name());
    //                 traveler.setEmail(travelerDTO.email());
    //                 traveler.setCpf(travelerDTO.cpf());
    //                 traveler.setBirthDate(travelerDTO.birthDate());
    //                 traveler.setReservation(null);
    //                 return traveler;
    //             }).collect(Collectors.toList());
    //     Payment payment = new Payment();
    //     payment.setValuePaid(dto.payment().valuePaid());
    //     payment.setPaymentMethod(dto.payment().paymentMethod());
    //     payment.setStatus(dto.payment().status());
    //     payment.setTax(dto.payment().tax());
    //     payment.setInstallment(dto.payment().installment());
    //     payment.setInstallmentAmount(dto.payment().installmentAmount());
    //     Reservation reservation = new Reservation();
    //     reservation.setReservationDate(dto.reservationDate());
    //     reservation.setUser(user);
    //     reservation.setPackageDate(packageDate);
    //     reservation.setSituation(ReservationSituation.confirmada);
    //     reservation.setTravelers(travelers);
    //     reservation.setPayment(payment);
    //     travelers.forEach(t -> t.setReservation(reservation));
    //     return reservationRepository.save(reservation);
    // }
    @Override
    public ReservationSearchResultDTO findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return reservationRepository.findById(id)
                .map(reservationMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
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

    private void handleErrorSQL(DataIntegrityViolationException e) {
        Throwable rootCause = e.getRootCause();
        String message = (rootCause != null && rootCause.getMessage() != null)
                ? rootCause.getMessage()
                : e.getMessage();
        log.error("Database integrity error: {}", message);
        throw new BusinessException("Falha ao criar reserva: " + message);
    }
}
