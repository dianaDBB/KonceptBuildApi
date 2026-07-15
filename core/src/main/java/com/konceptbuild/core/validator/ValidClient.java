package com.konceptbuild.core.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ClientValidator.class})
public @interface ValidClient {
    String message() default "Invalid client";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
