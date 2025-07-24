// UpdatePackageDateDTO.java
package com.orvian.travelapi.controller.dto.packagedate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "DTO para atualização de datas de pacote")
public record UpdatePackageDateDTO(

        @NotNull(message = "Data de início não pode ser nula")
        @FutureOrPresent(message = "Data de início deve ser hoje ou no futuro")
        @Schema(description = "Data de início do pacote", example = "2024-07-01")
        LocalDate startDate,

        @NotNull(message = "Data de fim não pode ser nula")
        @FutureOrPresent(message = "Data de fim deve ser hoje ou no futuro")
        @Schema(description = "Data de término do pacote", example = "2024-07-10")
        LocalDate endDate,

        @Min(value = 1, message = "Quantidade disponível deve ser pelo menos 1")
        @Schema(description = "Quantidade disponível para o pacote", example = "20")
        int qtd_available,

        @Schema(description = "ID do pacote de viagem relacionado", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
        UUID travelPackageId
) {}