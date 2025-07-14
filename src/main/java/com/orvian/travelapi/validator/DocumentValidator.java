package com.orvian.travelapi.validator;

import com.orvian.travelapi.annotation.Document;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentValidator implements ConstraintValidator<Document, String> {
    @Override
    public boolean isValid(String document, ConstraintValidatorContext constraintValidatorContext) {
        return document != null && (isValidCPF(document) || isValidPassport(document));
    }

    private boolean isValidCPF(String document) {
        return document.matches("[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}-[0-9]{2}");
    }

    private boolean isValidPassport(String document) {
        return document.matches("[A-Z]{2}[0-9]{6}");
    }
}
