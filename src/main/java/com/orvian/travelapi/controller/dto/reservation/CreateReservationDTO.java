package com.orvian.travelapi.controller.dto.reservation;

import com.orvian.travelapi.controller.dto.payment.CreatePaymentDTO;
import com.orvian.travelapi.controller.dto.traveler.CreateTravelerDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateReservationDTO(
        LocalDate reservationDate,
        UUID userId,
        UUID packageDateId,
        List<CreateTravelerDTO> travelers,
        CreatePaymentDTO payment
) {
}