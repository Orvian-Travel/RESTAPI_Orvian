package com.orvian.travelapi.annotation;

import com.orvian.travelapi.validator.DocumentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DocumentValidator.class)
@Target( {ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Document {

    String message() default "The document must be a valid CPF or passport (e.g., 123.456.789-00 or AZ123456).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
