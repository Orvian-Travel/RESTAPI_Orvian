package com.orvian.travelapi.controller.dto.traveler;

import com.orvian.travelapi.annotation.Adult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(
        name = "UpdateTravelerDTO",
        description = "Data Transfer Object para atualizar um viajante",
        title = "atualizar um viajante DTO"
)
public record UpdateTravelerDTO(
        @Size(max = 150, message = "Name must be at most 150 characters long")
        @Schema(name = "name", description = "Nome completo do viajante", example = "John Doe")
        String name,

        @Size(max = 150, message = "Email must be at most 150 characters long")
        @Schema(name = "email", description = "Email do viajante", example = "Example@email.com")
        String  email,

        @Size(min = 14, max = 14, message = "CPF must be 14 characters long")
        @Schema(name = "cpf", description = "CPF do viajante", example = "John Doe")
        String cpf,

        @Adult
        @Schema(name = "birthDate", description = "Data de nascimento do viajante no formato ISO (AAAA-MM-DD)", example = "1990-01-01")
        LocalDate birthDate
) {
}
