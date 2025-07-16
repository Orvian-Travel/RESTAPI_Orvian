package com.orvian.travelapi.controller.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

/*
         DTO para o resultado de uma busca de usuário.
         O @Schema é usado para documentar o DTO na API OpenAPI com Swagger..
         Criei esse DTO para retornar apenas os dados necessários de um usuário, assim não expondo informações sensíveis como senha.
 */

@Schema(
        name = "UserSearchResultDTO",
        description = "Data Transfer Object for user search results",
        title = "User Search Result DTO"
)
public record UserSearchResultDTO(
        @Schema(name = "id", description = "User's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(name = "name", description = "User's full name", example = "John Doe")
        String name,
        @Schema(name = "email", description = "User's email address", example = "example@example.com")
        String email,
        @Schema(name = "phone", description = "User's phone number", example = "(12) 34567-8901")
        String phone,
        @Schema(name = "document", description = "User's document number", example = "123.456.789-10 or AZ123456")
        String document,
        @Schema(name = "birthDate", description = "User's date of birth in ISO format (YYYY-MM-DD)", example = "1990-01-01")
        LocalDate birthDate
) {
}
