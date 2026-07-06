package com.iplion.films.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("app.scheduler.grab-kinopoisk")
@Validated
public record KinopoiskGrabSchedulerProperties(
    boolean enabled,

    @NotBlank(message = "Cron for Kinopoisk scheduler is not configured")
    String cron,

    @Min(value = 1, message = "Film limit must be greater than 0")
    int itemsNum
) {
}
