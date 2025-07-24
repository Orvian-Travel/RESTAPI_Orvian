package com.orvian.travelapi.controller.dto.traveler;

import com.orvian.travelapi.annotation.Adult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(
        name = "CreateTravelerDTO",
        description = "Data Transfer Object para criar um novo viajante",
        title = "Criar um Viajante DTO"
)
public record CreateTravelerDTO (

        @NotBlank(message = "name is required")
        @Size(max = 150, message = "Name must be at most 150 characters long")
        @Schema(name = "name", description = "Nome completo do viajante", example = "John Doe")
        String name,

        @NotBlank(message = "email is required")
        @Size(max = 150, message = "Email must be at most 150 characters long")
        @Schema(name = "email", description = "Email do viajante", example = "Example@email.com")
        String  email,

        @NotBlank(message = "cpf is required")
        @Size(min = 14, max = 14, message = "CPF must be 14 characters long")
        @Schema(name = "cpf", description = "CPF do viajante", example = "123.456.789-10")
        String cpf,

        @NotBlank(message = "birthdate is required")
        @Adult
        @Schema(name = "birthDate", description = "Data de nascimento do viajante no formato ISO (AAAA-MM-DD)", example = "1990-01-01")
        LocalDate birthDate
){
}
