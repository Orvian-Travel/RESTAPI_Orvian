package com.orvian.travelapi.controller.dto.media;

import jakarta.validation.constraints.NotNull;

public record CreateMediaDTO(
        @NotNull(message = "Content64 não pode ser nulo")
        String content64,
        @NotNull(message = "Type não pode ser nulo")
        String type
) {
}