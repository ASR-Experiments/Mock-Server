package com.asr.example.mock.server.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ErrorResponse {

    String timestamp;
    String path;
    int status;
    String error;
    String requestId;
    Exception exception;
}
