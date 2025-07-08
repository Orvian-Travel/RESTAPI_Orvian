package com.orvian.travelapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.util.List;

@Schema(
        name = "ResponseErrorDTO",
        description = "Data Transfer Object for API response errors",
        title = "Response Error DTO"
)
public record ResponseErrorDTO(
        @Schema(name = "status", description = "HTTP status code of the error response", example = "400")
        int status,
        @Schema(name = "message", description = "Detailed error message", example = "Invalid request data")
        String message,
        @Schema(name = "errors", description = "List of field-specific validation errors, if any")
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
