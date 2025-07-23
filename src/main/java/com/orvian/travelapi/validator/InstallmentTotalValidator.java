package com.orvian.travelapi.validator;

import java.math.BigDecimal;

import com.orvian.travelapi.annotation.InstallmentTotalValid;
import com.orvian.travelapi.controller.dto.payment.CreatePaymentDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InstallmentTotalValidator implements ConstraintValidator<InstallmentTotalValid, CreatePaymentDTO> {

    @Override
    public boolean isValid(CreatePaymentDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        BigDecimal installmentAmount = dto.installmentAmount();
        Integer installment = dto.installment();
        BigDecimal valuePaid = dto.valuePaid();

        if (installmentAmount == null || installment == null) {
            return true;
        }

        boolean valid = installmentAmount.multiply(BigDecimal.valueOf(installment)).compareTo(valuePaid) <= 0;
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "The total installment amount cannot be greater than the amount paid."
            ).addPropertyNode("installmentAmount") // ou outro campo relacionado
                    .addConstraintViolation();
        }
        return valid;
    }
}
