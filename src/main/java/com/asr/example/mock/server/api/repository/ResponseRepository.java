package com.asr.example.mock.server.api.repository;

import com.asr.example.mock.server.api.entity.ResponseEntity;
import com.asr.example.mock.server.api.model.interfaces.ResponseWithEndpoint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ResponseRepository extends R2dbcRepository<ResponseEntity, Long> {

    /**
     * Custom HQL method to find the response by response ID along with endpoint details by left joining Response Entity with Endpoint Entity.
     */
    @Query("""
            SELECT r.responseId, r.responseBody, r.responseHeaders, r.httpStatus as responseStatus, \
            e.id AS endpointId, e.endpoint, e.httpMethod \
            FROM response r LEFT JOIN endpoint e ON r.endpoint_id = e.endpoint_id \
            WHERE r.id = :responseId""")
    Mono<ResponseWithEndpoint> findEndpointByResponseId(Long responseId);

    Flux<ResponseEntity> findByEndpointId(Long endpointId, Pageable pageable);

    Mono<Long> countByEndpointId(Long endpointId);

    Mono<Void> deleteByEndpointId(Long endpointId);

    Mono<ResponseEntity> findFirstByEndpointIdAndIsActiveOrderByPriorityAsc(Long endpointId, boolean isActive);
}
