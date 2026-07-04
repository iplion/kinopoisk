package com.iplion.films.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final KinopoiskProperties props;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .connectTimeout(Duration.ofSeconds(5))
            .readTimeout(Duration.ofSeconds(10))
            .rootUri(props.baseUrl())
            .defaultHeader("X-API-KEY", props.apiKey())
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}
