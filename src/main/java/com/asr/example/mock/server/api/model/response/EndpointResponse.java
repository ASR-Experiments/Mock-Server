package com.asr.example.mock.server.api.model.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EndpointResponse(
        String endpoint,
        String method,
        Long endpointId,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy,
        Long version
) {
}
