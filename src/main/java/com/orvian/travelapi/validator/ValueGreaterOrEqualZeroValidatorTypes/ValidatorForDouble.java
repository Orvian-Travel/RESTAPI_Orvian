package com.orvian.travelapi.validator.ValueGreaterOrEqualZeroValidatorTypes;

import com.orvian.travelapi.annotation.ValueGreaterOrEqualZero;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidatorForDouble implements ConstraintValidator<ValueGreaterOrEqualZero, Double> {

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Allow null values
        }
        return value >= 0; // Value must be greater than or equal to zero
    }

}
