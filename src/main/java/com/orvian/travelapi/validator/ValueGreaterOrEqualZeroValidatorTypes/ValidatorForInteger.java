package com.orvian.travelapi.validator.ValueGreaterOrEqualZeroValidatorTypes;

import com.orvian.travelapi.annotation.ValueGreaterOrEqualZero;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidatorForInteger implements ConstraintValidator<ValueGreaterOrEqualZero, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Allow null values
        }
        return value >= 0; // Value must be greater than or equal to zero
    }

}
