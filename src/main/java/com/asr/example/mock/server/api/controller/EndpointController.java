package com.asr.example.mock.server.api.controller;

import com.asr.example.mock.server.api.model.request.EndpointRequest;
import com.asr.example.mock.server.api.model.response.EndpointResponse;
import com.asr.example.mock.server.api.service.EndpointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/endpoint")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class EndpointController {

    EndpointService endpointService;

    @PostMapping
    public Mono<ResponseEntity<EndpointResponse>> createEndpoint(@Valid @RequestBody EndpointRequest request) {
        return endpointService.createEndpoint(request)
            .doOnSuccess(entity -> log.info("Endpoint saved: {}", entity))
                .doOnError(throwable -> log.error("Error saving endpoint: {}", throwable.getMessage(), throwable))
            .map(ResponseEntity::ok);
    }

    @GetMapping
    public Mono<ResponseEntity<EndpointResponse>> getEndpoint(@RequestParam Long id, @RequestParam(required = false) Boolean isActive) {
        return endpointService.getEndpoint(id, isActive)
            .doOnSuccess(entity -> {
                if (entity != null) log.info("Retrieved endpoint: {}", entity.endpointId());
            })
            .doOnError(throwable -> log.error("Error retrieving endpoint: {}", throwable.getMessage(), throwable))
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping("/query")
    public Mono<ResponseEntity<EndpointResponse>> getEndpointByMethodAndPath(
        @Valid @RequestBody EndpointRequest request,
        @RequestParam(required = false) Boolean isActive) {
        return endpointService.getEndpointByMethodAndPath(request, isActive)
            .doOnSuccess(entity -> {
                if (entity != null) log.info("Retrieved endpoint: {}", entity.endpointId());
            })
            .doOnError(throwable -> log.error("Error retrieving endpoint: {}", throwable.getMessage(), throwable))
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @PatchMapping
    public Mono<ResponseEntity<EndpointResponse>> updateEndpoint(@RequestBody EndpointRequest request,
                                                                 @RequestParam Long id) {
        return endpointService.updateEndpoint(request, id)
            .doOnSuccess(entity -> log.info("Endpoint updated: {}", entity))
            .doOnError(throwable -> log.error("Error updating endpoint: {}", throwable.getMessage(), throwable))
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> deleteEndpoint(@RequestParam Long id) {
        return endpointService.deleteEndpoint(id)
            .doOnSuccess(entity -> log.info("Endpoint deleted: {}", entity))
            .doOnError(throwable -> log.error("Error deleting endpoint: {}", throwable.getMessage(), throwable))
            .<ResponseEntity<Void>>then(Mono.just(ResponseEntity.accepted().build()))
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }
}
