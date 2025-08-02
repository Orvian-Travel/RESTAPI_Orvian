package com.orvian.travelapi.controller.dto.reservation;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO contendo data de reserva disponível para filtro")
public record ReservationDateDTO(
        @Schema(description = "Data em que foram feitas reservas", example = "2025-01-15")
        LocalDate reservationDate
        ) {

}
