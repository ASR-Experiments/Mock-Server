package com.asr.example.mock.server.api.service;

import com.asr.example.mock.server.api.model.request.EndpointRequest;
import com.asr.example.mock.server.api.model.response.EndpointResponse;
import reactor.core.publisher.Mono;

public interface EndpointService {

    Mono<EndpointResponse> createEndpoint(EndpointRequest request);

    Mono<EndpointResponse> getEndpoint(Long id, final Boolean isActive);

    Mono<EndpointResponse> getEndpointByMethodAndPath(EndpointRequest request, final Boolean isActive);

    Mono<EndpointResponse> updateEndpoint(EndpointRequest request, Long id);

    Mono<EndpointResponse> deleteEndpoint(Long id);
}
