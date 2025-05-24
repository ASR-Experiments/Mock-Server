package com.asr.example.mock.server.api.model.request;

import com.asr.example.mock.server.api.validator.ValidResponse;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;

import java.math.BigInteger;

@ValidResponse
public record ResponseRequest(
        String endpoint,
        String method,
        BigInteger endpointId,
        @NotEmpty String body,
        String headers,
        @NotEmpty HttpStatus status
) {
}
