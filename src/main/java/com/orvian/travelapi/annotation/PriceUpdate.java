package com.orvian.travelapi.annotation;

import com.orvian.travelapi.validator.PriceUpdateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PriceUpdateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PriceUpdate {

    String message() default "Price must be at least 10.0.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
