package com.asr.example.mock.server.api.service;

import com.asr.example.mock.server.api.model.interfaces.ResponseWithEndpoint;
import com.asr.example.mock.server.api.model.request.EndpointRequest;
import com.asr.example.mock.server.api.model.request.ResponseRequest;
import com.asr.example.mock.server.api.model.response.PageData;
import com.asr.example.mock.server.api.model.response.ResponseResponse;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface ResponseService {

    Mono<ResponseResponse> getResponse(Long responseId);

    Mono<ResponseResponse> getTopResponseForEndpoint(Long endpointId);

    Mono<ResponseWithEndpoint> getResponseWithEndpoint(Long responseId);

    Mono<PageData<ResponseResponse>> getAllResponsesForEndpoint(
        Pageable pageable, Long endpointId);

    Mono<ResponseResponse> createResponse(ResponseRequest request);

    Mono<ResponseResponse> updateResponse(Long responseId, ResponseRequest response);

    Mono<Void> deleteResponse(Long responseId);

    Mono<Void> deleteAllResponsesForEndpoint(Long endpointId);

    Mono<Void> deleteAllResponsesForEndpoint(EndpointRequest request);
}
