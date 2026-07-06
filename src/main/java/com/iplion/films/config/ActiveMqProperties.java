package com.iplion.films.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@ConfigurationProperties("app.activemq")
@Validated
public record ActiveMqProperties(
    @NotEmpty(message = "Queues of ActiveMQ is not setup.")
    Map<@NotBlank String, @NotBlank String> queues
) {
    public static final String GRABBED_FILMS_QUEUE_KEY = "grabbed-films";

    public String getQueueName(String queueKey) {
        String queueName = queues.get(queueKey);

        if (queueName == null) {
            throw new IllegalStateException("Queue of ActiveMQ is not setup: " + queueKey);
        }

        return queueName;
    }
}
