package com.orvian.travelapi.validator;

import com.orvian.travelapi.annotation.DurationUpdate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DurationUpdateValidator implements ConstraintValidator<DurationUpdate, Integer> {

    @Override
    public boolean isValid(Integer duration, ConstraintValidatorContext constraintValidatorContext) {
        if (duration == null) return true;
        return duration >= 1;
    }
}
