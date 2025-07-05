package com.orvian.travelapi.controller.dto;

public record FieldErrorDTO(
        String field,
        String error
) {
}
