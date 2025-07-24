package com.orvian.travelapi.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.orvian.travelapi.validator.DurationUpdateValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = DurationUpdateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationUpdate {

    String message() default "Duration must be at least 1 day.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
