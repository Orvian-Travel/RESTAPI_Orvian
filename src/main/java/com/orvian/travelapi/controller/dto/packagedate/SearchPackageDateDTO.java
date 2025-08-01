package com.orvian.travelapi.controller.dto.packagedate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de busca de data de pacote com informações do pacote")
public record SearchPackageDateDTO(
        @Schema(description = "ID da data do pacote")
        UUID id,
        @Schema(description = "Data de início", example = "2024-08-15")
        LocalDate startDate,
        @Schema(description = "Data de término", example = "2024-08-22")
        LocalDate endDate,
        @Schema(description = "Quantidade disponível", example = "20")
        Integer qtd_available,
        @Schema(description = "ID do pacote de viagem")
        UUID travelPackageId,
        // ✅ NOVOS CAMPOS: Informações do TravelPackage
        @Schema(description = "Título do pacote", example = "Pacote Família em Gramado - Inverno 2025")
        String packageTitle,
        @Schema(description = "Destino do pacote", example = "Gramado, RS")
        String packageDestination,
        @Schema(description = "Duração em dias", example = "5")
        Integer packageDuration,
        @Schema(description = "Data de criação")
        LocalDateTime createdAt,
        @Schema(description = "Data de atualização")
        LocalDateTime updatedAt
        ) {

}
