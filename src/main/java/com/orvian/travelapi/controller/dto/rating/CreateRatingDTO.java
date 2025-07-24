package com.orvian.travelapi.controller.dto.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRatingDTO(
        @Min(value = 1, message = "Rate must be at least 1")
        @Max(value = 5, message = "Rate must be at most 5")
        int rate,
        @NotBlank
        @Max(value = 250, message = "Comment must be at most 250 characters")
        String comment,
        @NotNull
        UUID reservationId
) {
}
