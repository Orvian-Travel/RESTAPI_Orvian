package com.orvian.travelapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.orvian.travelapi.controller.dto.reservation.ReservationDateDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationSearchResultDTO;
import com.orvian.travelapi.domain.enums.ReservationSituation;
import com.orvian.travelapi.domain.model.Reservation;

public interface ReservationService extends CrudService<UUID, Reservation> {

    @Override
    Page<ReservationSearchResultDTO> findAll(Integer pageNumber, Integer pageSize, UUID userID);

    @Override
    ReservationSearchResultDTO findById(UUID id);

    Page<ReservationSearchResultDTO> findAllByStatusAndDate(Integer pageNumber, Integer pageSize,
            UUID userId, ReservationSituation status, LocalDate reservationDate);

    List<ReservationDateDTO> findAvailableReservationDates(UUID userId);

    byte[] exportReservationsToExcel();

    byte[] exportReservationsToPdf();
}
