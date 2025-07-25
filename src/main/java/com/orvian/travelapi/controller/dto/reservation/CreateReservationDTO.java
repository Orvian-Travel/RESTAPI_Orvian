package com.orvian.travelapi.controller.dto.reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.orvian.travelapi.controller.dto.payment.CreatePaymentDTO;
import com.orvian.travelapi.controller.dto.traveler.CreateTravelerDTO;
import com.orvian.travelapi.domain.enums.ReservationSituation;

import jakarta.validation.Valid;

public record CreateReservationDTO(
        ReservationSituation situation,
        LocalDate reservationDate,
        UUID userId,
        List<CreateTravelerDTO> travelers,
        @Valid
        CreatePaymentDTO payment,
        UUID packageDateId
        ) {

}
