package com.orvian.travelapi.controller.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.orvian.travelapi.domain.enums.PaymentMethod;
import com.orvian.travelapi.domain.enums.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

public record PaymentSearchResultDTO(
        @Schema(name = "valuePaid", description = "Valor pago no pagamento", example = "100.00")
        BigDecimal valuePaid,
        @Schema(name = "paymentMethod", description = "Método de pagamento utilizado", example = "CRÉDITO")
        @Enumerated(EnumType.STRING)
        PaymentMethod paymentMethod,
        @Schema(name = "status", description = "Status atual do pagamento", example = "APROVADO")
        @Enumerated(EnumType.STRING)
        PaymentStatus status,
        @Schema(name = "paymentApprovedAt", description = "Data e hora em que o pagamento foi aprovado", example = "2023-10-01T12:00:00")
        @Temporal(TemporalType.TIMESTAMP)
        Date paymentApprovedAt,
        @Schema(name = "tax", description = "Taxa aplicada ao pagamento", example = "10.00%")
        Double tax,
        @Schema(name = "installment", description = "Installment number for the payment", example = "1")
        Integer installment,
        @Schema(name = "installmentAmount", description = "Installment amount for the payment", example = "100.00")
        BigDecimal installmentAmount,
        @Schema(name = "createdAt", description = "Timestamp when the PAYMENT was created", example = "2023-10-01T12:00:00")
        @Temporal(TemporalType.TIMESTAMP)
        LocalDateTime createdAt,
        @Schema(name = "updatedAt", description = "Timestamp when the PAYMENT was last updated", example = "2023-10-01T12:00:00")
        @Temporal(TemporalType.TIMESTAMP)
        LocalDateTime updatedAt
        ) {

}
