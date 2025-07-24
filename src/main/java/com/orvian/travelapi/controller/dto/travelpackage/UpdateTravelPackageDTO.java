package com.orvian.travelapi.controller.dto.travelpackage;

import java.math.BigDecimal;

import com.orvian.travelapi.annotation.DurationUpdate;
import com.orvian.travelapi.annotation.MaxPeopleUpdate;
import com.orvian.travelapi.annotation.PriceUpdate;

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
