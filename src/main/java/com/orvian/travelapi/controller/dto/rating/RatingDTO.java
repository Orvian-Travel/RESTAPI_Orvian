package com.orvian.travelapi.controller.dto.rating;

import java.time.LocalDate;
import java.util.UUID;

public record RatingDTO(
        UUID id,
        int rate,
        String comment,
        LocalDate createdAt,
        LocalDate updatedAt,
        UUID reservationId,
        UUID userId,
        String userName,
        UUID travelPackageId,
        String travelPackageTitle
) {}