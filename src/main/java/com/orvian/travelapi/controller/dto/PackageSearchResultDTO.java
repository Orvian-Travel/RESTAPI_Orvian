package com.orvian.travelapi.controller.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record PackageSearchResultDTO(
        UUID id,
        String title,
        String description,
        String destination,
        int duration,
        BigDecimal price,
        int maxPeople,
        Date createdAt,
        Date updatedAt
) {
}
