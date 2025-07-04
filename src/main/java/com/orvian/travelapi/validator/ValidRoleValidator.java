package com.orvian.travelapi.validator;

import com.orvian.travelapi.annotation.ValidRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Valid;

import java.util.Set;

public class ValidRoleValidator implements ConstraintValidator<ValidRole, String> {
    private static final Set<String> VALID_ROLES = Set.of("ADMIN", "USER", "ATENDENTE");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && VALID_ROLES.contains(value.toUpperCase());
    }
}
