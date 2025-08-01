package com.orvian.travelapi.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.orvian.travelapi.controller.dto.reservation.ReservationSearchResultDTO;
import com.orvian.travelapi.domain.enums.ReservationSituation;
import com.orvian.travelapi.domain.model.Reservation;

public interface ReservationService extends CrudService<UUID, Reservation> {

    @Override
    Page<ReservationSearchResultDTO> findAll(Integer pageNumber, Integer pageSize, UUID userID);

    @Override
    ReservationSearchResultDTO findById(UUID id);

    Page<ReservationSearchResultDTO> findAllByStatus(Integer pageNumber, Integer pageSize,
            UUID userId, ReservationSituation status);
}
