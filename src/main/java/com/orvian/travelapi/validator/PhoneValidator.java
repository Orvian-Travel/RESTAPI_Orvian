package com.orvian.travelapi.validator;

import com.orvian.travelapi.annotation.Phone;
import jakarta.validation.ConstraintValidator;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    @Override
    public boolean isValid(String phone, jakarta.validation.ConstraintValidatorContext constraintValidatorContext) {
        if (phone == null) return true; // Permite null para updates
        return phone.matches("\\(\\d{2}\\) \\d{5}-\\d{4}");
    }
}
