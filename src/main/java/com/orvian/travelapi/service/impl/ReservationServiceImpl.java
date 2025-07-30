package com.orvian.travelapi.service.impl;

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
import com.orvian.travelapi.controller.dto.reservation.ReservationSearchResultDTO;
import com.orvian.travelapi.controller.dto.reservation.UpdateReservationDTO;
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
                        return reservationMapper.toDTO(reservation, payment);
                    });

        } catch (Exception e) {
            log.error("Erro ao buscar reservas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar reservas: " + e.getMessage());
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
        reservationRepository.findById(id)
                .map(reservation -> {
                    reservationRepository.delete(reservation);
                    return reservation;
                })
                .orElseThrow(() -> new NotFoundException("Reservation not found with ID: " + id));
    }

}
