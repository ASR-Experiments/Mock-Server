package com.asr.example.mock.server.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Filter which halts the request, if response is supposed to be mocked
 */
@Slf4j
@Component
public class LocalResponseFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /*
        This routeName is needed later for identifying the request
         */
        String routeName = Optional
                .ofNullable(exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR))
                .map(String::valueOf)
                .orElse("<Unable to determine route>");

        log.info("Local Response Filter applied at route {}", routeName);
        /*
        This condition is currently false for testing, later will be fetched dynamically from the configuration
         */
        if (false) {
            return chain.filter(exchange);
        } else {
            // Get response attribute
            final ServerHttpResponse response = exchange.getResponse();
            // Prepare response
            byte[] responseBytes = """
                    {
                        "message": "Intercepted"
                    }
                    """
                    .getBytes(StandardCharsets.UTF_8);
            response.getHeaders().set(HttpHeaders.CONTENT_LENGTH, String.valueOf(responseBytes.length));
            DataBuffer wrappedResponse = response.bufferFactory()
                    .wrap(responseBytes);
            // Prepare headers and status
            response.getHeaders().set("x-intercepted", "true");
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.setStatusCode(HttpStatus.OK);
            // As app is reactive, we will wait for serialization to complete the response and return as is , rather than continuing the chain
            return response
                    .writeWith(Mono.just(wrappedResponse))
                    .then(Mono.create(ignore -> log.info("Mutated Response")));
        }
    }

    @Override
    public int getOrder() {
        /*
         * Doesn't want to run any logic if request is getting mocked, and hence the highest precedence
         */
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
