package com.orvian.travelapi.controller.dto.travelpackage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.orvian.travelapi.controller.dto.packagedate.SearchPackageDateDTO;

public record PackageSearchResultDTO(
        UUID id,
        String title,
        String description,
        String destination,
        int duration,
        BigDecimal price,
        int maxPeople,
        List<SearchPackageDateDTO> packageDates,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

}
