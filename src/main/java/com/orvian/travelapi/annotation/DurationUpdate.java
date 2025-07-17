package com.orvian.travelapi.annotation;

import com.orvian.travelapi.validator.DurationUpdateValidator;
import com.orvian.travelapi.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DurationUpdateValidator.class)
@Target( {ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationUpdate {
    String message() default "Duration must be at least 1 day.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
