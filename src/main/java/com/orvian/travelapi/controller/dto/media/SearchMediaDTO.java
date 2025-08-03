package com.orvian.travelapi.controller.dto.media;

import java.util.UUID;

public record SearchMediaDTO(
        UUID id,
        String type,
        String contentType
) {
}
