package com.asr.example.mock.server.api.model.response;

import lombok.Builder;

import java.math.BigInteger;

@Builder
public record ResponseResponse(
        String response,
        String contentType,
        Integer statusCode,
        BigInteger responseId,
        BigInteger endpointId,
        String createdBy,
        String updatedBy,
        Long createdAt,
        Long updatedAt
) {
}
