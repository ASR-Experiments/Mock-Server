package com.asr.example.mock.server.api.service.impl;

import com.asr.example.mock.server.api.entity.EndpointEntity;
import com.asr.example.mock.server.api.mapper.EndpointMapper;
import com.asr.example.mock.server.api.model.request.EndpointRequest;
import com.asr.example.mock.server.api.model.response.EndpointResponse;
import com.asr.example.mock.server.api.repository.EndpointRepository;
import com.asr.example.mock.server.api.service.EndpointService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EndpointServiceImpl implements EndpointService {

    EndpointMapper endpointMapper;
    EndpointRepository endpointRepository;

    @Override
    public Mono<EndpointResponse> createEndpoint(EndpointRequest request) {
        EndpointEntity endpointEntity = endpointMapper.mapEntity(request);
        return endpointRepository.save(endpointEntity)
            .map(endpointMapper::mapResponse);
    }

    @Override
    public Mono<EndpointResponse> getEndpoint(Long id) {
        return endpointRepository.findById(id)
            .map(endpointMapper::mapResponse);
    }

    @Override
    public Mono<EndpointResponse> getEndpointByMethodAndPath(EndpointRequest request) {
        return endpointRepository.findByMethodAndEndpoint(request.method(), request.endpoint())
            .map(endpointMapper::mapResponse);
    }

    @Override
    public Mono<EndpointResponse> updateEndpoint(EndpointRequest request, Long id) {
        return endpointRepository.findById(id)
            .flatMap(existingEntity -> {
                endpointMapper.mapEntity(request, existingEntity);
                existingEntity.setNew(false);
                return endpointRepository.save(existingEntity);
            })
            .map(endpointMapper::mapResponse);
    }

    @Override
    public Mono<EndpointResponse> deleteEndpoint(Long id) {
        return endpointRepository.findById(id)
            .flatMap(existingEntity -> endpointRepository.delete(existingEntity)
                .thenReturn(endpointMapper.mapResponse(existingEntity)));
    }
}