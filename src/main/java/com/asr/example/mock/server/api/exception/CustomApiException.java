package com.asr.example.mock.server.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

import java.util.Arrays;
import java.util.List;

public class CustomApiException extends Exception {

    private final List<String> details;
    @Getter
    private final HttpStatusCode statusCode;

    protected CustomApiException(
        List<String> details,
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace,
        HttpStatusCode statusCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.details = details;
        this.statusCode = statusCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ApiError.ApiErrorBuilder getApiError() {
        return ApiError.builder()
            .message(getMessage())
            .stackTrace(Arrays.stream(getStackTrace()).map(StackTraceElement::toString).toList())
            .errorDetails(details)
            .statusCode(statusCode);
    }

    public static class Builder {

        private List<String> details;
        private String message;
        private Throwable cause;
        private boolean enableSuppression = false;
        private boolean writableStackTrace = true;
        private HttpStatusCode statusCode;

        public Builder details(List<String> details) {
            this.details = details;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public Builder enableSuppression(boolean enableSuppression) {
            this.enableSuppression = enableSuppression;
            return this;
        }

        public Builder writableStackTrace(boolean writableStackTrace) {
            this.writableStackTrace = writableStackTrace;
            return this;
        }

        public Builder statusCode(HttpStatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public CustomApiException build() {
            return new CustomApiException(details, message, cause, enableSuppression, writableStackTrace, statusCode);
        }
    }
}
