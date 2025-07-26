package com.orvian.travelapi.controller.dto.reservation;

import java.time.LocalDate;

import com.orvian.travelapi.domain.enums.ReservationSituation;

public record UpdateReservationDTO(
        ReservationSituation situation,
        LocalDate reservationDate
        ) {

}
