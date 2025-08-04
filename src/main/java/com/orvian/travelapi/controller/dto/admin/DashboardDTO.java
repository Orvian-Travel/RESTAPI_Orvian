package com.orvian.travelapi.controller.dto.admin;

import com.orvian.travelapi.controller.dto.travelpackage.PaymentByPackageDTO;

import java.util.List;

public record DashboardDTO(
        Integer newUsers,
        WeekRatingDTO weekRating,
        List<PaymentByPackageDTO> salesByPackage
) {
}
