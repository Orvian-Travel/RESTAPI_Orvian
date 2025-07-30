package com.orvian.travelapi.controller.dto.packagedate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

import com.orvian.travelapi.annotation.FutureOrPresentUTC;

@Schema(description = "DTO para criação de datas de pacote")
public record CreatePackageDateDTO(
        @NotNull(message = "Data de início não pode ser nula")
        @FutureOrPresentUTC(message = "Data de início deve ser hoje ou no futuro (UTC)")
        @Schema(description = "Data de início do pacote", example = "2024-07-01")
        LocalDate startDate,
        @NotNull(message = "Data de fim não pode ser nula")
        @FutureOrPresentUTC(message = "Data de fim deve ser hoje ou no futuro (UTC)")
        @Schema(description = "Data de término do pacote", example = "2024-07-10")
        LocalDate endDate,
        @Min(value = 1, message = "Quantidade disponível deve ser pelo menos 1")
        @Schema(description = "Quantidade disponível para o pacote", example = "20")
        int qtd_available
        ) {

}
