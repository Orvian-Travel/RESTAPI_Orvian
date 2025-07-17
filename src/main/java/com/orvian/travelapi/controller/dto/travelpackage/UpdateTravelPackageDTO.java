package com.orvian.travelapi.controller.dto.travelpackage;

import com.orvian.travelapi.annotation.DurationUpdate;
import com.orvian.travelapi.annotation.MaxPeopleUpdate;
import com.orvian.travelapi.annotation.PriceUpdate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateTravelPackageDTO(
        String title,
        String description,
        String destination,
        @DurationUpdate
        Integer duration,
        @PriceUpdate
        BigDecimal price,
        @MaxPeopleUpdate
        Integer maxPeople
) {
}

