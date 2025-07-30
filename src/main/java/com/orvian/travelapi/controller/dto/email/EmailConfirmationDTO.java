package com.orvian.travelapi.controller.dto.email;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para transportar dados necessários para confirmaçao de pagamento via
 * email Contém informações do usuário, reserva e pagamento aprovado
 */
@Schema(description = "Dados para envio de email de confirmação de pagamento")
public record EmailConfirmationDTO(
        @Schema(description = "ID único da reserva confirmada", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID reservationId,
        @Schema(description = "Nome completo do cliente", example = "João Silva")
        String customerName,
        @Schema(description = "Email do destinatário", example = "joao@email.com")
        String customerEmail,
        @Schema(description = "Título do pacote de viagem", example = "Viagem Incrível para Paris")
        String packageTitle,
        @Schema(description = "Destino da viagem", example = "Paris, França")
        String packageDestination,
        @Schema(description = "Data de início da viagem", example = "2024-08-15")
        LocalDate tripStartDate,
        @Schema(description = "Data de término da viagem", example = "2024-08-22")
        LocalDate tripEndDate,
        @Schema(description = "Valor total pago", example = "2500.00")
        BigDecimal totalAmountPaid,
        @Schema(description = "Método de pagamento utilizado", example = "CARTAO_CREDITO")
        String paymentMethod,
        @Schema(description = "Data/hora da aprovação do pagamento")
        LocalDateTime paymentApprovedAt,
        @Schema(description = "Número total de viajantes", example = "2")
        Integer totalTravelers
        ) {

}
