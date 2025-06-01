package com.asr.example.mock.server.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class PaginationWebMVCSupport implements WebFluxConfigurer {

    @Bean
    public PageRequest defaultPageRequest() {
        return PageRequest.of(0, 10);
    }

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        ReactiveSortHandlerMethodArgumentResolver sortResolver =
            new ReactiveSortHandlerMethodArgumentResolver();
        sortResolver.setSortParameter("sort");

        ReactivePageableHandlerMethodArgumentResolver pageableResolver =
            new ReactivePageableHandlerMethodArgumentResolver(sortResolver);
        pageableResolver.setFallbackPageable(defaultPageRequest());
        pageableResolver.setPageParameterName("page");
        pageableResolver.setSizeParameterName("size");
        configurer.addCustomResolver(pageableResolver);
    }
}
