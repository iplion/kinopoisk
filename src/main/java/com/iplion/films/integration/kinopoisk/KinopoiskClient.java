package com.iplion.films.integration.kinopoisk;

import com.iplion.films.exception.KinopoiskClientException;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskItemsRequestDto;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskItemsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KinopoiskClient {

    private static final String FILMS_PATH = "/api/v2.2/films";

    private final RestTemplate restTemplate;

    public KinopoiskItemsResponseDto getItems(KinopoiskItemsRequestDto request) {
        try {
            return restTemplate.getForObject(
                buildFilmsUri(request),
                KinopoiskItemsResponseDto.class
            );
        } catch (HttpStatusCodeException e) {
            throw mapStatusException(e);
        } catch (RestClientException e) {
            throw new KinopoiskClientException(
                "Ошибка соединения с Kinopoisk API",
                HttpStatus.BAD_GATEWAY,
                e
            );
        }
    }

    private String buildFilmsUri(KinopoiskItemsRequestDto request) {
        return UriComponentsBuilder
            .fromPath(FILMS_PATH)
            .queryParamIfPresent("countries", optionalIntegerList(request.countries()))
            .queryParamIfPresent("genres", optionalIntegerList(request.genres()))
            .queryParamIfPresent("order", Optional.ofNullable(request.order()))
            .queryParamIfPresent("type", Optional.ofNullable(request.type()))
            .queryParamIfPresent("ratingFrom", Optional.ofNullable(request.ratingFrom()))
            .queryParamIfPresent("ratingTo", Optional.ofNullable(request.ratingTo()))
            .queryParamIfPresent("yearFrom", Optional.ofNullable(request.yearFrom()))
            .queryParamIfPresent("yearTo", Optional.ofNullable(request.yearTo()))
            .queryParamIfPresent("imdbId", optionalString(request.imdbId()))
            .queryParamIfPresent("keyword", optionalString(request.keyword()))
            .queryParamIfPresent("page", Optional.ofNullable(request.page()))
            .build()
            .toUriString();
    }

    private KinopoiskClientException mapStatusException(HttpStatusCodeException e) {
        return switch (e.getStatusCode().value()) {
            case 401 -> new KinopoiskClientException(
                "Пустой или неправильный токен Kinopoisk API",
                HttpStatus.BAD_GATEWAY,
                e
            );
            case 402 -> new KinopoiskClientException(
                "Превышен лимит запросов(или дневной, или общий) Kinopoisk API",
                HttpStatus.BAD_GATEWAY,
                e
            );
            case 429 -> new KinopoiskClientException(
                "Слишком много запросов к Kinopoisk API. Лимит 5 запросов в секунду",
                HttpStatus.TOO_MANY_REQUESTS,
                e
            );
            default -> new KinopoiskClientException(
                "Ошибка Kinopoisk API: " + e.getStatusCode(),
                HttpStatus.BAD_GATEWAY,
                e
            );
        };
    }

    private Optional<String> optionalIntegerList(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
            values.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","))
        );
    }

    private Optional<String> optionalString(String value) {
        return value == null || value.isBlank()
            ? Optional.empty()
            : Optional.of(value.trim());
    }
}
