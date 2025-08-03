package com.orvian.travelapi.controller.dto.travelpackage;

import java.math.BigDecimal;

public record PaymentByPackageDTO(
        String destination,
        String name,
        Integer reservationYear,
        Integer reservationWeek,
        Integer confirmedReservationsCount,
        BigDecimal approvedPaymentsSum
) {
}
