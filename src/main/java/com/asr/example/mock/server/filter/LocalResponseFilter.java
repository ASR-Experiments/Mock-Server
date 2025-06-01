package com.asr.example.mock.server.filter;

import com.asr.example.mock.server.api.model.request.EndpointRequest;
import com.asr.example.mock.server.api.model.response.EndpointResponse;
import com.asr.example.mock.server.api.model.response.ResponseResponse;
import com.asr.example.mock.server.api.service.EndpointService;
import com.asr.example.mock.server.api.service.ResponseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Filter which halts the request, if response is supposed to be mocked
 */
@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LocalResponseFilter implements GlobalFilter, Ordered {

    EndpointService endpointService;
    ResponseService responseService;

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        /*
        This routeName is needed later for identifying the request
         */
        String routeName = Optional
            .ofNullable(exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR))
            .map(String::valueOf)
            .orElse("<Unable to determine route>");
        String predicate = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ATTR);
        log.info("Local Response Filter applied at route {}", routeName);
        /*
        This condition is currently false for testing, later will be fetched dynamically from the configuration
         */
        String path = removePredicatePrefix(exchange.getRequest().getPath().value(), predicate);

        EndpointRequest endpointQuery = EndpointRequest.builder()
            .method(exchange.getRequest().getMethod().name())
            .endpoint(path)
            .build();
        Mono<EndpointResponse> endpoint = endpointService.getEndpointByMethodAndPath(endpointQuery, true);

        return endpoint
            .doOnNext(val -> log.info("1. Endpoint next: {}", val))
            .doOnError(err -> log.error("1. Endpoint Error: {}", err.getMessage(), err))
            .doOnSuccess(val -> log.info("1. Endpoint Success: {}", val))
            .flatMap(endpointDetails -> responseService.getTopResponseForEndpoint(endpointDetails.endpointId()))
            .doOnNext(val -> log.info("2. Response next: {}", val))
            .doOnError(err -> log.error("2. Response Error: {}", err.getMessage(), err))
            .doOnSuccess(val -> log.info("2. Response Success: {}", val))
            .flatMap(responseDetails -> this.prepareResponse(exchange, responseDetails))
            .onErrorResume(err -> {
                log.error("Error in LocalResponseFilter pipeline, delegating to chain.filter: {}", err.getMessage(), err);
                return chain.filter(exchange);
            })
            .switchIfEmpty(chain.filter(exchange))
            .then();
    }

    String removePredicatePrefix(final String path, final String predicate) {
        if (!StringUtils.hasText(predicate)) {
            return path;
        }
        // Remove common section from path and return the remaining part
        PathContainer pathContainer = PathContainer.parsePath(path);
        PathContainer predicateContainer = PathContainer.parsePath(predicate);
        List<PathContainer.Element> pathElements = pathContainer.elements();
        List<PathContainer.Element> predicateElements = predicateContainer.elements();
        int commonLength = Math.min(pathElements.size(), predicateElements.size());
        int i = 0;
        for (; i < commonLength; i++) {
            if (!pathElements.get(i).value().equals(predicateElements.get(i).value())) {
                break;
            }
        }
        // Return the remaining part of the path after the common prefix
        return pathElements.subList(i, pathElements.size())
            .stream()
            .map(PathContainer.Element::value)
            .collect(Collectors.joining());
    }

    @Override
    public int getOrder() {
        /*
         * Doesn't want to run any logic if request is getting mocked, and hence the highest precedence
         */
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public Mono<Object> prepareResponse(final ServerWebExchange exchange, ResponseResponse proxyResponse) {
        // Get response attribute
        final ServerHttpResponse response = exchange.getResponse();
        // Prepare response
        byte[] responseBytes = proxyResponse.responseBody()
            .getBytes(StandardCharsets.UTF_8);
        response.getHeaders().remove(HttpHeaders.CONTENT_LENGTH);
        response.getHeaders().remove(HttpHeaders.CONTENT_TYPE);
        response.getHeaders().remove(HttpHeaders.CONTENT_ENCODING);
        DataBuffer wrappedResponse = response.bufferFactory()
            .wrap(responseBytes);
        // Prepare headers and status
        response.getHeaders().set("x-intercepted", "true");
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getHeaders().set(HttpHeaders.CONTENT_LENGTH, String.valueOf(responseBytes.length));
        response.getHeaders().set(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.displayName());
        response.setStatusCode(proxyResponse.statusCode());
        // As app is reactive, we will wait for serialization to complete the response and return as is , rather than continuing the chain
        return response
            .writeWith(Mono.just(wrappedResponse))
            .then(Mono.create(ignore -> log.info("Mutated Response")));
    }

}
