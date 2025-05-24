package com.asr.example.mock.server.api.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record EndpointRequest(
        @NotEmpty String endpoint,
        @NotEmpty String method
) {
}
