package com.orvian.travelapi.annotation;

import com.orvian.travelapi.validator.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target( {ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Phone {

    String message() default "The phone number must be valid (e.g., (12) 34567-8910).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
