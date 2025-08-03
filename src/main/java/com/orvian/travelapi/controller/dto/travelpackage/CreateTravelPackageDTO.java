package com.orvian.travelapi.controller.dto.travelpackage;

import java.math.BigDecimal;
import java.util.List;

import com.orvian.travelapi.controller.dto.media.CreateMediaDTO;
import com.orvian.travelapi.controller.dto.packagedate.CreatePackageDateDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTravelPackageDTO(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotBlank
        String destination,
        @Min(value = 1, message = "Duration must be at least 1 day")
        int duration,
        @NotNull
        @DecimalMin(value = "10.0", message = "Price must be at least 10.0")
        BigDecimal price,
        @Min(value = 1, message = "Max people must be at least 1")
        int maxPeople,
        @Valid
        List<CreatePackageDateDTO> packageDates,
        @Valid
        List<CreateMediaDTO> medias
        ) {

}
