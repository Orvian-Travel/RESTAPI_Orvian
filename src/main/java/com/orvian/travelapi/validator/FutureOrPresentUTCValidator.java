package com.orvian.travelapi.validator;

import com.orvian.travelapi.annotation.FutureOrPresentUTC;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class FutureOrPresentUTCValidator implements ConstraintValidator<FutureOrPresentUTC, LocalDate> {

    @Override
    public void initialize(FutureOrPresentUTC constraintAnnotation) {
        // Inicialização se necessário
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate currentUTCDate = LocalDate.now(ZoneOffset.UTC);

        return value.isEqual(currentUTCDate) || value.isAfter(currentUTCDate);
    }
}
