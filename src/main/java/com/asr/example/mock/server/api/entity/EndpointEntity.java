package com.asr.example.mock.server.api.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record EndpointEntity(
        @Id BigInteger endpointId,
        String endpoint,
        String method,
        @CreatedBy String createdBy,
        @CreatedDate LocalDateTime createdAt,
        @LastModifiedBy String updatedBy,
        @LastModifiedDate LocalDateTime updatedAt
) {
}
