package com.orvian.travelapi.controller.dto.travelpackage;

import java.math.BigDecimal;
import java.util.List;

import com.orvian.travelapi.annotation.DurationUpdate;
import com.orvian.travelapi.annotation.MaxPeopleUpdate;
import com.orvian.travelapi.annotation.PriceUpdate;
import com.orvian.travelapi.controller.dto.packagedate.UpdatePackageDateDTO;

import jakarta.validation.Valid;

public record UpdateTravelPackageDTO(
        String title,
        String description,
        String destination,
        @DurationUpdate
        Integer duration,
        @PriceUpdate
        BigDecimal price,
        @MaxPeopleUpdate
        Integer maxPeople,
        @Valid
        List<UpdatePackageDateDTO> packageDates
        ) {

}
