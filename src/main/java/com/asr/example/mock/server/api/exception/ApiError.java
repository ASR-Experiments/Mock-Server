package com.asr.example.mock.server.api.exception;

import lombok.Builder;
import org.springframework.http.HttpStatusCode;

import java.util.List;

@Builder
public record ApiError(
    HttpStatusCode statusCode,
    String requestId,
    String message,
    String description,
    String path,
    String timestamp,
    List<String> errorDetails,
    List<String> stackTrace,
    ApiError cause
) {

}
