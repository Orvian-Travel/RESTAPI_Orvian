package com.orvian.travelapi.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.orvian.travelapi.validator.ValueGreaterOrEqualZeroValidatorTypes.ValidatorForBIgDecimal;
import com.orvian.travelapi.validator.ValueGreaterOrEqualZeroValidatorTypes.ValidatorForDouble;
import com.orvian.travelapi.validator.ValueGreaterOrEqualZeroValidatorTypes.ValidatorForInteger;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = {ValidatorForBIgDecimal.class, ValidatorForDouble.class, ValidatorForInteger.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueGreaterOrEqualZero {

    String message() default "The value must be greather than or equal 0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
