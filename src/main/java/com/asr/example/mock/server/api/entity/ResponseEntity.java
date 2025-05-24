package com.asr.example.mock.server.api.entity;

import io.r2dbc.spi.Clob;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.http.HttpStatus;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record ResponseEntity(
        @Id BigInteger responseId,
        BigInteger endpointId,
        Boolean isActive,
        HttpStatus httpStatus,
        Clob responseBody,
        Clob responseHeaders,
        @CreatedBy String createdBy,
        @CreatedDate LocalDateTime createdAt,
        @LastModifiedBy String updatedBy,
        @LastModifiedDate LocalDateTime updatedAt
) {
}
