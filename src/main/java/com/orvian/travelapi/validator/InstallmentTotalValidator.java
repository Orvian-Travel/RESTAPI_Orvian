package com.orvian.travelapi.validator;

import java.math.BigDecimal;

import com.orvian.travelapi.annotation.InstallmentTotalValid;
import com.orvian.travelapi.domain.model.Payment;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InstallmentTotalValidator implements ConstraintValidator<InstallmentTotalValid, Payment> {

    @Override
    public boolean isValid(Payment payment, ConstraintValidatorContext context) {
        if (payment == null) {
            return true;
        }
        BigDecimal installmentAmount = payment.getInstallmentAmount();
        Integer installment = payment.getInstallment();
        BigDecimal valuePaid = payment.getValuePaid();

        if (installmentAmount == null || installment == null || valuePaid == null) {
            return true;
        }
        return installmentAmount.multiply(BigDecimal.valueOf(installment)).compareTo(valuePaid) <= 0;
    }
}
