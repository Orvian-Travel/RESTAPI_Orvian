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
        @Schema(name = "id", description = "Identificação única do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(name = "name", description = "Nome completo do usuário", example = "John Doe")
        String name,
        @Schema(name = "email", description = "Endereço de email do usuário", example = "exemplo@exemplo.com")
        String email,
        @Schema(name = "phone", description = "Número de celular do usuário", example = "(12) 34567-8910")
        String phone,
        @Schema(name = "document", description = "Documento de identificação do usuário", example = "123.456.789-10 ou AZ123456")
        String document,
        @Schema(name = "birthDate", description = "Data de nascimento do usuário no formato ISO (AAAA-MM-DD)", example = "1990-01-01")
        LocalDate birthDate
) {
}
