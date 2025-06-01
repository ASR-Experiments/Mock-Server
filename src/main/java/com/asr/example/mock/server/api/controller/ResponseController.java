package com.asr.example.mock.server.api.controller;

import com.asr.example.mock.server.api.model.interfaces.ResponseWithEndpoint;
import com.asr.example.mock.server.api.model.request.EndpointRequest;
import com.asr.example.mock.server.api.model.request.ResponseRequest;
import com.asr.example.mock.server.api.model.response.PageData;
import com.asr.example.mock.server.api.model.response.ResponseResponse;
import com.asr.example.mock.server.api.service.ResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/response")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ResponseController {

    ResponseService responseService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ResponseResponse>> getResponse(@PathVariable Long id) {
        return responseService.getResponse(id)
            .doOnSuccess(r -> log.info("Fetched response: {}", r))
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @GetMapping("/{id}/with-endpoint")
    public Mono<ResponseEntity<ResponseWithEndpoint>> getResponseWithEndpoint(@PathVariable Long id) {
        return responseService.getResponseWithEndpoint(id)
            .doOnSuccess(r -> log.info("Fetched response with endpoint: {}", r))
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @GetMapping("/endpoint/{endpointId}")
    public Mono<ResponseEntity<PageData<ResponseResponse>>> getAllResponsesForEndpoint(
        @PathVariable Long endpointId,
        Pageable pageable) {
        return responseService.getAllResponsesForEndpoint(pageable, endpointId)
            .doOnSuccess(r -> log.info("Fetched responses for endpoint {}: {}", endpointId, r))
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping
    public Mono<ResponseEntity<ResponseResponse>> createResponse(
        @Valid @RequestBody ResponseRequest request) {
        return responseService.createResponse(request)
            .doOnSuccess(r -> log.info("Created response: {}", r))
            .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<ResponseResponse>> updateResponse(
        @PathVariable Long id,
        @Valid @RequestBody ResponseRequest request) {
        return responseService.updateResponse(id, request)
            .doOnSuccess(r -> log.info("Updated response: {}", r))
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteResponse(@PathVariable Long id) {
        return responseService.deleteResponse(id)
            .doOnSuccess(v -> log.info("Deleted response: {}", id))
            .<ResponseEntity<Void>>then(Mono.just(ResponseEntity.accepted().build()))
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping("/endpoint/{endpointId}")
    public Mono<ResponseEntity<Void>> deleteAllResponsesForEndpoint(
        @PathVariable Long endpointId) {
        return responseService.deleteAllResponsesForEndpoint(endpointId)
            .doOnSuccess(v -> log.info("Deleted all responses for endpoint: {}", endpointId))
            .<ResponseEntity<Void>>then(Mono.just(ResponseEntity.accepted().build()))
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping("/endpoint")
    public Mono<ResponseEntity<Void>> deleteAllResponsesForEndpointByRequest(
        @Valid @RequestBody EndpointRequest request) {
        return responseService.deleteAllResponsesForEndpoint(request)
            .doOnSuccess(v -> log.info("Deleted all responses for endpoint by request: {}", request))
            .<ResponseEntity<Void>>then(Mono.just(ResponseEntity.accepted().build()))
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }
}
