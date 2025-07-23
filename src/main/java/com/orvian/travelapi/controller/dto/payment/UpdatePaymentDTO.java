package com.orvian.travelapi.controller.dto.payment;

import java.math.BigDecimal;

import com.orvian.travelapi.annotation.InstallmentTotalValid;
import com.orvian.travelapi.annotation.ValueGreaterOrEqualZero;
import com.orvian.travelapi.domain.enums.PaymentMethod;
import com.orvian.travelapi.domain.enums.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@InstallmentTotalValid
@Schema(name = "UpdatePaymentDTO", description = "Data Transfer Object para atualizar pagamento", title = "Criar um Pagamento DTO")
public record UpdatePaymentDTO(
        @Size(max = 10, message = "Value paid must be at most 10 characters long")
        @Schema(name = "valuePaid", description = "Valor pago no pagamento", example = "100.00")
        @ValueGreaterOrEqualZero
        BigDecimal valuePaid,
        @Size(max = 15, message = "Payment method must be at most 15 characters long")
        @Schema(name = "paymentMethod", description = "Método de pagamento utilizado", example = "CRÉDITO")
        PaymentMethod paymentMethod,
        @Size(max = 15, message = "Status must be at most 15 characters long")
        @Schema(name = "status", description = "Status atual do pagamento", example = "APROVADO")
        PaymentStatus status,
        @Schema(name = "tax", description = "Taxa aplicada ao pagamento", example = "10.00%")
        @ValueGreaterOrEqualZero
        Double tax,
        @Schema(name = "installment", description = "Installment number for the payment", example = "1")
        @ValueGreaterOrEqualZero
        Integer installment,
        @Size(max = 10, message = "InstallmentAmount must be at most 10 characters long")
        @Schema(name = "installmentAmount", description = "Installment amount for the payment", example = "100.00")
        @ValueGreaterOrEqualZero
        BigDecimal installmentAmount
        ) {

}
