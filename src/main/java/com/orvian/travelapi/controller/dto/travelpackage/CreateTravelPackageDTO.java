package com.orvian.travelapi.controller.dto.travelpackage;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTravelPackageDTO(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotBlank
        String destination,
        @Min(value = 1, message = "Duration must be at least 1 day")
        int duration,
        @NotNull @DecimalMin(value = "10.0", message = "Price must be at least 10.0")
        BigDecimal price,
        @Min(value = 1, message = "Max people must be at least 1")
        int maxPeople
        ) {
}
