package com.asr.example.mock.server.api.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ResponseValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidResponse {
    String message() default "Either both endpoint and method must be present, or endpointId must be present";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}