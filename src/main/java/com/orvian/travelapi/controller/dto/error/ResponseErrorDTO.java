package com.orvian.travelapi.controller.dto.error;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;

/*
    DTO para erros de resposta da API.
    Utilizado para padronizar a estrutura de erros retornados pela API.
    Inclui o status HTTP, mensagem de erro, timestamp, path e uma lista de erros de campo, se houver.
 */
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
        List<FieldErrorDTO> errors,
        @Schema(name = "timestamp", description = "Date and time when the error occurred", example = "2025-07-22T15:00:00")
        LocalDateTime timestamp,
        @Schema(name = "path", description = "API endpoint where the error occurred", example = "/api/payments")
        String path
        ) {

    public static ResponseErrorDTO defaultResponse(String message, String path) {
        return new ResponseErrorDTO(HttpStatus.BAD_REQUEST.value(), message, List.of(), LocalDateTime.now(), path);
    }

    public static ResponseErrorDTO conflict(String message, String path) {
        return new ResponseErrorDTO(HttpStatus.CONFLICT.value(), message, List.of(), LocalDateTime.now(), path);
    }

    public static ResponseErrorDTO unprocessableEntity(String message, List<FieldErrorDTO> errors, String path) {
        return new ResponseErrorDTO(HttpStatus.UNPROCESSABLE_ENTITY.value(), message, errors, LocalDateTime.now(), path);
    }

    public static ResponseErrorDTO of(HttpStatus status, String message, List<FieldErrorDTO> errors, String path) {
        return new ResponseErrorDTO(status.value(), message, errors, LocalDateTime.now(), path);
    }
}
