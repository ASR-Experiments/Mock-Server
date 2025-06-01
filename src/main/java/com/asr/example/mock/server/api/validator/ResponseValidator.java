package com.asr.example.mock.server.api.validator;

import com.asr.example.mock.server.api.model.request.ResponseRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;


public class ResponseValidator implements ConstraintValidator<ValidResponse, ResponseRequest> {

    @Override
    public boolean isValid(ResponseRequest value, ConstraintValidatorContext context) {
        if (value == null) return true; // Let @NotNull handle nulls

        boolean endpointAndMethodPresent = StringUtils.hasText(value.endpoint()) && StringUtils.hasText(value.method());
        boolean endpointIdPresent = value.endpointId() != null;

        return endpointAndMethodPresent || endpointIdPresent;
    }
}