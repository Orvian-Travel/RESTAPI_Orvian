package com.orvian.travelapi.controller.dto.user;

import com.orvian.travelapi.annotation.ValidRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

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
        LocalDate birthDate,
        @ValidRole
        @Schema(name = "role", description = "Cargo do usuário (apenas ADMIN pode alterar)",
                example = "USER", allowableValues = {"USER", "ATENDENTE", "ADMIN"})
        String role
) {
}
