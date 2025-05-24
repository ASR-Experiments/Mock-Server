package com.asr.example.mock.server.api.controller;

import com.asr.example.mock.server.api.entity.EndpointEntity;
import com.asr.example.mock.server.api.mapper.EndpointMapper;
import com.asr.example.mock.server.api.model.request.EndpointRequest;
import com.asr.example.mock.server.api.model.response.EndpointResponse;
import com.asr.example.mock.server.api.repository.EndpointRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/endpoint")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class EndpointController {

    EndpointMapper endpointMapper;

    EndpointRepository endpointRepository;

    @PostMapping
    public Mono<EndpointResponse> handleRequest(@Valid @RequestBody EndpointRequest request) {
        EndpointEntity endpointEntity = endpointMapper.mapEntity(request);
        return endpointRepository.save(endpointEntity)
                .doOnSuccess(entity -> log.error("Endpoint saved: {}", entity))
                .doOnError(throwable -> log.error("Error saving endpoint: {}", throwable.getMessage(), throwable))
                .map(endpointMapper::mapResponse);
    }
}
