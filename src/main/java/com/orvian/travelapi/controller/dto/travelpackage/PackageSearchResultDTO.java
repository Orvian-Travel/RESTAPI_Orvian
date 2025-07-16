package com.orvian.travelapi.controller.dto.travelpackage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PackageSearchResultDTO(
        UUID id,
        String title,
        String description,
        String destination,
        int duration,
        BigDecimal price,
        int maxPeople,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
