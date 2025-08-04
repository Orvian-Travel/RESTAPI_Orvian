package com.orvian.travelapi.controller.dto.rating;

import java.util.UUID;

public record CreateRatingDTO(
        int rate,
        String comment,
        UUID reservationId
) {}