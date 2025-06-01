package com.asr.example.mock.server.api.model.response;

import lombok.Builder;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;


@Builder
public record ResponseResponse(
    String responseBody,
    HttpStatusCode statusCode,
    String responseHeaders,
    Long responseId,
    Long endpointId,
        String createdBy,
        String updatedBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long version
) {
}
