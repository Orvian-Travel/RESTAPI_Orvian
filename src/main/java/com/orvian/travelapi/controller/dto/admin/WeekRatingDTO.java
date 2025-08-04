package com.orvian.travelapi.controller.dto.admin;

public record WeekRatingDTO (
        Integer currentRating,
        Integer beforeRating,
        Integer percentage
){}
