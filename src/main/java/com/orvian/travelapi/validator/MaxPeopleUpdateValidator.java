package com.orvian.travelapi.validator;

import com.orvian.travelapi.annotation.MaxPeopleUpdate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxPeopleUpdateValidator implements ConstraintValidator<MaxPeopleUpdate, Integer> {

    @Override
    public boolean isValid(Integer maxPeople, ConstraintValidatorContext constraintValidatorContext) {
        if (maxPeople == null) return true;
        return maxPeople >= 1;
    }
}
