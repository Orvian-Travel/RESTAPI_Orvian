package com.orvian.travelapi.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import com.orvian.travelapi.validator.FutureOrPresentUTCValidator;

@Documented
@Constraint(validatedBy = FutureOrPresentUTCValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrPresentUTC {

    String message() default "Data deve ser hoje ou no futuro (baseado em UTC)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
