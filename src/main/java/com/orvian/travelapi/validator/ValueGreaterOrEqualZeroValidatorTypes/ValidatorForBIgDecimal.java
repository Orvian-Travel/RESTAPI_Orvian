package com.orvian.travelapi.validator.ValueGreaterOrEqualZeroValidatorTypes;

import java.math.BigDecimal;

import com.orvian.travelapi.annotation.ValueGreaterOrEqualZero;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidatorForBIgDecimal implements ConstraintValidator<ValueGreaterOrEqualZero, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Allow null values
        }
        return value.compareTo(BigDecimal.ZERO) >= 0;
    }
}
