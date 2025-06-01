package com.asr.example.mock.server.api.model.request;

import com.asr.example.mock.server.api.validator.ValidResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatusCode;


@ValidResponse
public record ResponseRequest(
        String endpoint,
        String method,
        Long endpointId,
        Long priority,
        @NotEmpty String body,
        String headers,
        @NotNull HttpStatusCode status
) {
}
