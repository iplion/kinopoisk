package com.iplion.films.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("app.kinopoisk")
@Validated
public record KinopoiskProperties(
    @NotBlank
    String baseUrl,

    @NotBlank
    String apiKey
) {
}
