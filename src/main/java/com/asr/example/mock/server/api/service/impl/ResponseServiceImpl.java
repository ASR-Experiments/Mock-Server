package com.asr.example.mock.server.api.service.impl;

import com.asr.example.mock.server.api.entity.ResponseEntity;
import com.asr.example.mock.server.api.exception.CustomApiException;
import com.asr.example.mock.server.api.mapper.ResponseMapper;
import com.asr.example.mock.server.api.model.interfaces.ResponseWithEndpoint;
import com.asr.example.mock.server.api.model.request.EndpointRequest;
import com.asr.example.mock.server.api.model.request.ResponseRequest;
import com.asr.example.mock.server.api.model.response.PageData;
import com.asr.example.mock.server.api.model.response.ResponseResponse;
import com.asr.example.mock.server.api.repository.EndpointRepository;
import com.asr.example.mock.server.api.repository.ResponseRepository;
import com.asr.example.mock.server.api.service.ResponseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ResponseServiceImpl implements ResponseService {

    ResponseRepository responseRepository;
    EndpointRepository endpointRepository;
    ResponseMapper responseMapper;

    private static CustomApiException getCustomApiException(Throwable ex) {
        String rootCause = Optional.ofNullable(NestedExceptionUtils.getRootCause(ex))
            .orElse(ex)
            .getLocalizedMessage();
        if (rootCause.toLowerCase().contains("fk_response_for_endpoint")) {
            return CustomApiException.builder()
                .message("Endpoint not found for the provided response")
                .statusCode(HttpStatus.BAD_REQUEST)
                .cause(ex.getCause())
                .details(List.of(rootCause))
                .build();
        }
        return CustomApiException.builder()
            .message(ex.getMessage())
            .cause(ex.getCause())
            .details(List.of(rootCause))
            .build();
    }

    @Override
    public Mono<ResponseResponse> getResponse(Long responseId) {
        return responseRepository.findById(responseId)
            .map(responseMapper::mapResponse);
    }

    @Override
    public Mono<ResponseResponse> getTopResponseForEndpoint(final Long endpointId) {
        return responseRepository.findFirstByEndpointIdAndIsActiveOrderByPriorityAsc(endpointId, true)
            .map(responseMapper::mapResponse);
    }

    @Override
    public Mono<ResponseWithEndpoint> getResponseWithEndpoint(Long responseId) {
        return responseRepository.findEndpointByResponseId(responseId);
    }

    @Override
    public Mono<PageData<ResponseResponse>> getAllResponsesForEndpoint(
        Pageable pageRequest, Long endpointId) {
        return responseRepository.findByEndpointId(endpointId, pageRequest)
            .collectList()
            .zipWith(responseRepository.countByEndpointId(endpointId),
                (responseList, totalCount) -> convertToPageData(responseList, totalCount, pageRequest)
            )
            .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<ResponseResponse> createResponse(ResponseRequest request) {
        Mono<ResponseEntity> responseEntityMono;
        if (request.endpointId() == null) {
            responseEntityMono = endpointRepository.findByMethodAndEndpoint(
                    request.method(), request.endpoint()
                )
                .switchIfEmpty(Mono.error(
                    CustomApiException.builder()
                        .message("Endpoint not found for the provided response")
                        .statusCode(HttpStatus.BAD_REQUEST)
                        .build()
                ))
                .map(entity -> {
                    ResponseEntity responseEntity = responseMapper.mapEntity(request);
                    responseEntity.setEndpointId(entity.getId());
                    return responseEntity;
                });
        } else {
            ResponseEntity responseEntity = responseMapper.mapEntity(request);
            responseEntityMono = Mono.just(responseEntity);
        }
        return responseEntityMono
            .flatMap(responseRepository::save)
            .onErrorMap(
                ex -> ex instanceof DataIntegrityViolationException,
                ResponseServiceImpl::getCustomApiException
            )
            .map(responseMapper::mapResponse);
    }

    @Override
    public Mono<ResponseResponse> updateResponse(Long responseId, ResponseRequest request) {
        return responseRepository.findById(responseId)
            .flatMap(existingResponse -> {
                responseMapper.mapEntity(request, existingResponse);
                existingResponse.setNew(false);
                return responseRepository.save(existingResponse);
            })
            .map(responseMapper::mapResponse)
            .switchIfEmpty(Mono.error(new RuntimeException("Response not found")));
    }

    @Override
    public Mono<Void> deleteResponse(Long responseId) {
        return responseRepository.deleteById(responseId)
            .then();
    }

    @Override
    public Mono<Void> deleteAllResponsesForEndpoint(Long endpointId) {
        return responseRepository.deleteByEndpointId(endpointId)
            .then();
    }

    @Override
    public Mono<Void> deleteAllResponsesForEndpoint(EndpointRequest request) {
        return endpointRepository.findByMethodAndEndpoint(request.method(), request.endpoint())
            .flatMap(endpoint -> responseRepository.deleteByEndpointId(endpoint.getId()))
            .then();
    }

    PageData<ResponseResponse> convertToPageData(
        List<ResponseEntity> currentPage, Long totalCount, Pageable pageRequest) {
        boolean hasNextPage = totalCount > (long) pageRequest.getPageSize() * (pageRequest.getPageNumber() + 1);
        return PageData.<ResponseResponse>builder()
            .currentPage(totalCount / pageRequest.getPageSize())
            .totalCount(totalCount)
            .pageSize(pageRequest.getPageSize())
            .pageNumber(pageRequest.getPageNumber())
            .hasNext(hasNextPage)
            .elements(currentPage.stream()
                .map(responseMapper::mapResponse)
                .collect(Collectors.toSet()
                ))
            .build();
    }
}
