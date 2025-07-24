package com.orvian.travelapi.controller.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

/*
    DTO para erros de validação de campos.
    Utilizado para retornar informações sobre erros específicos de campos em requisições.
 */

@Schema(
        name = "FieldErrorDTO",
        description = "Data Transfer Object for field validation errors",
        title = "Field Error DTO"
)
public record FieldErrorDTO(
        @Schema(name = "field", description = "The name of the field that caused the validation error", example = "email")
        String field,
        @Schema(name = "error", description = "The error message associated with the validation failure", example = "Email must be valid")
        String error
) {
}
