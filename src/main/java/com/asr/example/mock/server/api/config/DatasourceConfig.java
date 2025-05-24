package com.asr.example.mock.server.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Mono;

@Configuration
@EnableR2dbcAuditing(
        auditorAwareRef = "dataAuditorAware")
@EnableR2dbcRepositories(basePackages = {"com.asr.example.mock.server.api.repository"})
public class DatasourceConfig {

    @Bean(name = "dataAuditorAware")
    public ReactiveAuditorAware<String> dataAuditorAware() {
        return () -> System.getProperty("user.name") != null
                ? Mono.just(System.getProperty("user.name"))
                : Mono.empty();
    }
}
