package com.orvian.travelapi.controller.dto.packagedate;

import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para atualização de datas de pacote")
public record UpdatePackageDateDTO(
        @Schema(description = "ID da data do pacote (para atualização)", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
        UUID id,
        @NotNull(message = "Data de início não pode ser nula")

        @Schema(description = "Data de início do pacote", example = "2024-07-01")
        LocalDate startDate,
        @NotNull(message = "Data de fim não pode ser nula")

        @Schema(description = "Data de término do pacote", example = "2024-07-10")
        LocalDate endDate,
        @Min(value = 1, message = "Quantidade disponível deve ser pelo menos 1")
        @Schema(description = "Quantidade disponível para o pacote", example = "20")
        int qtd_available
        ) {

}
