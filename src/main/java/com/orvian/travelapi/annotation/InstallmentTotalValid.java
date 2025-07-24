package com.orvian.travelapi.annotation;

import com.orvian.travelapi.validator.InstallmentTotalValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InstallmentTotalValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InstallmentTotalValid {

    String message() default "Installment amount times installment must be less than or equal to value paid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
