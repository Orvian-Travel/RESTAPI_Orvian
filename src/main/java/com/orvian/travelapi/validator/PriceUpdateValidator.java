package com.orvian.travelapi.validator;

import com.orvian.travelapi.annotation.PriceUpdate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class PriceUpdateValidator implements ConstraintValidator<PriceUpdate, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal price, ConstraintValidatorContext constraintValidatorContext) {
        if (price == null) return true; // Allow null values
        return price.compareTo(BigDecimal.valueOf(10.0)) >= 0; // Price must be at least 10.0
    }
}
