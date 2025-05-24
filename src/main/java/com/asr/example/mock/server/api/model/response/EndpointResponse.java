package com.asr.example.mock.server.api.model.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EndpointResponse(
        String endpoint,
        String method,
        String endpointId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy
) {
}
