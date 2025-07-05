package com.orvian.travelapi.validator;

import com.orvian.travelapi.annotation.Adult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) return false;
        return !value.plusYears(18).isAfter(LocalDate.now());
    }
}
