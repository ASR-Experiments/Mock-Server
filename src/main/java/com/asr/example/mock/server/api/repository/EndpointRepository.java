package com.asr.example.mock.server.api.repository;

import com.asr.example.mock.server.api.entity.EndpointEntity;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface EndpointRepository extends R2dbcRepository<EndpointEntity, Long> {

    Mono<EndpointEntity> findByMethodAndEndpoint(String method, String path);

    Mono<EndpointEntity> findByIdAndIsActive(Long id, Boolean isActive);

    Mono<EndpointEntity> findByMethodAndEndpointAndIsActive(@NotEmpty String method, @NotEmpty String endpoint, Boolean isActive);
}
