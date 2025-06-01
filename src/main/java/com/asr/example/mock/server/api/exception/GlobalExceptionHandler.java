package com.asr.example.mock.server.api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
@Order(-2)
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GlobalExceptionHandler implements WebExceptionHandler {

    ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Exception occurred while processing request `{} {}`, error: {}",
            exchange.getRequest().getMethod(), exchange.getRequest().getPath(), ex.getMessage(), ex);
        ApiError apiError;
        if (ex instanceof CustomApiException cai) {
            apiError = cai.getApiError()
                .requestId(exchange.getRequest().getId())
                .path(exchange.getRequest().getPath().value())
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .statusCode(cai.getStatusCode() != null ? cai.getStatusCode() : HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        } else if (ex instanceof ConstraintViolationException cve) {
            apiError = handleConstraintViolationException(exchange, cve);
        } else if (ex instanceof MethodArgumentNotValidException manve) {
            apiError = handleMethodArgumentNotValidException(exchange, manve);
        } else if (ex instanceof ServerWebInputException swie) {
            apiError = handleServerWebInputException(exchange, swie);
        } else {
            apiError = ApiError.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .requestId(exchange.getRequest().getId())
                .path(exchange.getRequest().getPath().value())
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .stackTrace(Arrays.stream(ex.getStackTrace())
                    .map(StackTraceElement::toString)
                    .toList())
                .build();
        }
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(apiError.statusCode());
        return exchange.getResponse()
            .writeWith(Mono.just(exchange.getResponse()
                .bufferFactory()
                .wrap(objectMapper.writeValueAsBytes(apiError))));
    }

    private ApiError handleServerWebInputException(ServerWebExchange exchange, ServerWebInputException swie) {

        ApiError.ApiErrorBuilder apiErrorBuilder = ApiError.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .requestId(exchange.getRequest().getId())
            .message(swie.getMessage())
            .path(exchange.getRequest().getPath().value())
            .timestamp(String.valueOf(System.currentTimeMillis()))
            .errorDetails(
                swie.getReason() != null ? Arrays.asList(swie.getReason()) : null
            )
            .stackTrace(Arrays.stream(swie.getStackTrace())
                .map(StackTraceElement::toString)
                .toList());
        if (swie.getCause() != null) {
            apiErrorBuilder.cause(
                ApiError.builder()
                    .message(swie.getCause().getMessage())
                    .errorDetails(
                        swie.getCause().getMessage() != null ? Arrays.asList(swie.getCause().getMessage()) : null
                    )
                    .build()
            );
        }

        return apiErrorBuilder.build();
    }

    private ApiError handleMethodArgumentNotValidException(ServerWebExchange exchange, MethodArgumentNotValidException ex) {
        return ApiError.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .requestId(exchange.getRequest().getId())
            .message(ex.getMessage())
            .path(exchange.getRequest().getPath().value())
            .timestamp(String.valueOf(System.currentTimeMillis()))
            .errorDetails(
                ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> "'%s' has wrong value : '%s' since, %s"
                        .formatted(error.getField(), error.getRejectedValue(), error.getDefaultMessage()))
                    .toList()
            )
            .stackTrace(Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .toList())
            .build();
    }

    private ApiError handleConstraintViolationException(
        ServerWebExchange exchange, ConstraintViolationException ex) {
        return ApiError.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .requestId(exchange.getRequest().getId())
            .message(ex.getMessage())
            .path(exchange.getRequest().getPath().value())
            .timestamp(String.valueOf(System.currentTimeMillis()))
            .errorDetails(
                ex.getConstraintViolations()
                    .stream()
                    .map(error -> "'%s' has wrong value : '%s' since, %s"
                        .formatted(error.getPropertyPath(), error.getInvalidValue(), error.getMessage()))
                    .toList()
            )
            .stackTrace(Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .toList())
            .build();
    }
}
