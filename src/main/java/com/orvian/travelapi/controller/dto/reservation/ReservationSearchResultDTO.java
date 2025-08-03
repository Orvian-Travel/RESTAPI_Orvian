package com.orvian.travelapi.controller.dto.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.orvian.travelapi.controller.dto.media.SearchMediaDTO;
import com.orvian.travelapi.controller.dto.packagedate.SearchPackageDateDTO;
import com.orvian.travelapi.controller.dto.payment.PaymentSearchResultDTO;
import com.orvian.travelapi.controller.dto.traveler.TravelerSearchResultDTO;
import com.orvian.travelapi.controller.dto.user.UserSearchResultDTO;
import com.orvian.travelapi.domain.enums.ReservationSituation;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReservationSearchResultDTO(
        UUID id,
        LocalDate reservationDate,
        ReservationSituation situation,
        LocalDate cancelledDate,
        UserSearchResultDTO user,
        SearchPackageDateDTO packageDate,
        List<TravelerSearchResultDTO> travelers,
        PaymentSearchResultDTO payment,
        @Schema(description = "Primeira mídia do pacote de viagem")
        SearchMediaDTO firstMedia,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

}
