package com.orvian.travelapi.validator;

import com.orvian.travelapi.annotation.Phone;
import jakarta.validation.ConstraintValidator;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    @Override
    public boolean isValid(String phone, jakarta.validation.ConstraintValidatorContext constraintValidatorContext) {
        return phone != null && phone.matches("\\(\\d{2}\\) \\d{5}-\\d{4}");
    }
}
