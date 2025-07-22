package com.orvian.travelapi.controller.dto.traveler;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(
        name = "TravelerSearchResultDTO",
        description = "Data Transfer Object para resultado de busca dos viajantes",
        title = "retornar um viajante DTO"
)
public record TravelerSearchResultDTO(
        @Schema(name = "id", description = "Identificação única do viajante", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(name = "name", description = "Nome completo do viajante", example = "John Doe")
        String name,

        @Schema(name = "email", description = "Email do viajante", example = "Example@email.com")
        String email,

        @Schema(name = "cpf", description = "CPF do viajante", example = "John Doe")
        String cpf,

        @Schema(name = "birthDate", description = "Data de nascimento do viajante no formato ISO (AAAA-MM-DD)", example = "1990-01-01")
        LocalDate birthDate

) {
}
