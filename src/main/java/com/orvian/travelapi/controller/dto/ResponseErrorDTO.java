package com.orvian.travelapi.controller.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ResponseErrorDTO(
        int status,
        String message,
        List<FieldErrorDTO> errors
) {

    public static ResponseErrorDTO defaultResponse(String message) {
        return new ResponseErrorDTO(HttpStatus.BAD_REQUEST.value(), message, List.of());
    }

    public static ResponseErrorDTO conflict(String message) {
        return new ResponseErrorDTO(HttpStatus.CONFLICT.value(), message, List.of());
    }

    public static ResponseErrorDTO unprocessableEntity(String message) {
        return new ResponseErrorDTO(HttpStatus.UNPROCESSABLE_ENTITY.value(), message, List.of());
    }
}
