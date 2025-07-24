package com.orvian.travelapi.validator;

import java.math.BigDecimal;

import com.orvian.travelapi.annotation.InstallmentTotalValid;
import com.orvian.travelapi.controller.dto.payment.PaymentInstallmentData;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InstallmentTotalValidator implements ConstraintValidator<InstallmentTotalValid, PaymentInstallmentData> {

    @Override
    public boolean isValid(PaymentInstallmentData dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        try {
            BigDecimal installmentAmount = dto.installmentAmount();
            Integer installment = dto.installment();
            BigDecimal valuePaid = dto.valuePaid();

            if (installmentAmount == null || installment == null) {
                return true;
            }

            boolean valid = installmentAmount.multiply(BigDecimal.valueOf(installment)).compareTo(valuePaid) == 0;
            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "The total installment amount must be equal to the amount paid."
                ).addPropertyNode("installmentAmount").addConstraintViolation();
            }
            return valid;
        } catch (Exception e) {
            // Log and always return false to avoid 500
            System.out.println("Validator error: " + e.getMessage());
            return false;
        }
    }
}
