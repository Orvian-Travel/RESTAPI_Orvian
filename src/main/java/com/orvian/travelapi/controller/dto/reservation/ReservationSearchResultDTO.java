package com.orvian.travelapi.controller.dto.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.orvian.travelapi.controller.dto.packagedate.SearchPackageDateDTO;
import com.orvian.travelapi.controller.dto.payment.PaymentSearchResultDTO;
import com.orvian.travelapi.controller.dto.traveler.TravelerSearchResultDTO;
import com.orvian.travelapi.controller.dto.user.UserSearchResultDTO;
import com.orvian.travelapi.domain.enums.ReservationSituation;

public record ReservationSearchResultDTO(
        UUID id,
        LocalDate reservationDate,
        ReservationSituation situation,
        LocalDate cancelledDate,
        UserSearchResultDTO user,
        SearchPackageDateDTO packageDate,
        List<TravelerSearchResultDTO> travelers,
        PaymentSearchResultDTO payment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

}
