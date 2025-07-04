package com.orvian.travelapi.validator;

import com.orvian.travelapi.annotation.Adult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AdultValidator implements ConstraintValidator<Adult, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) return false;
        try {
            LocalDate birthDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return !birthDate.plusYears(18).isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
