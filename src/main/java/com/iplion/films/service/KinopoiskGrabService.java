package com.iplion.films.service;

import com.iplion.films.dto.FilmImportItemDto;
import com.iplion.films.dto.GrabFilmsRequestDto;
import com.iplion.films.dto.GrabFilmsResponseDto;
import com.iplion.films.integration.kinopoisk.KinopoiskClient;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskItemDto;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskItemsResponseDto;
import com.iplion.films.mapper.FilmImportItemMapper;
import com.iplion.films.mapper.KinopoiskItemsRequestDtoMapper;
import com.iplion.films.model.KinopoiskSearchOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class KinopoiskGrabService {

    private final KinopoiskClient kinopoiskClient;
    private final KinopoiskItemsRequestDtoMapper kinopoiskItemsRequestDtoMapper;
    private final FilmImportItemMapper filmImportItemMapper;
    private final FilmImportService filmImportService;

    @Transactional
    public GrabFilmsResponseDto grabFilms(GrabFilmsRequestDto request) {
        List<KinopoiskItemDto> items = extractItems(sendKinopoiskRequest(request));

        int savedFilmsCount = filmImportService.processNewItems(
            items.stream()
                .map(filmImportItemMapper::toFilmImportItemDto)
                .toList()
        );

        return new GrabFilmsResponseDto(
            items.size(),
            savedFilmsCount
        );
    }

    public KinopoiskItemsResponseDto sendKinopoiskRequest(GrabFilmsRequestDto request) {
        return kinopoiskClient.getItems(
            kinopoiskItemsRequestDtoMapper.toKinopoiskItemsRequest(request)
        );
    }

    private List<KinopoiskItemDto> extractItems(KinopoiskItemsResponseDto response) {
        return Objects.requireNonNullElse(response.items(), List.of());
    }

    public List<FilmImportItemDto> grabFilmsByGenreWithRatingOrder(int genre, int limit) {
        int page = 1;
        List<FilmImportItemDto> collectedItems = new ArrayList<>();

        log.info("Starting Kinopoisk grab: genre={}, limit={}", genre, limit);

        while (collectedItems.size() < limit) {
            var request = new GrabFilmsRequestDto(
                null,
                genre,
                KinopoiskSearchOrder.RATING,
                null, null, null, null, null, null, null,
                page
            );

            KinopoiskItemsResponseDto response = sendKinopoiskRequest(request);
            List<KinopoiskItemDto> items = extractItems(response);

            if (items.isEmpty()) {
                log.warn(
                    "Kinopoisk returned empty page: genre={}, page={}, collected={}, limit={}",
                    genre,
                    page,
                    collectedItems.size(),
                    limit
                );
                break;
            }

            collectedItems.addAll(
                items.stream()
                    .map(filmImportItemMapper::toFilmImportItemDto)
                    .toList()
            );

            if (response.total() != null && collectedItems.size() >= response.total()) {
                log.info(
                    "Reached Kinopoisk total: genre={}, collected={}, total={}",
                    genre,
                    collectedItems.size(),
                    response.total()
                );
                break;
            }

            page++;
        }

        List<FilmImportItemDto> limitedItems = collectedItems.stream()
            .limit(limit)
            .toList();

        log.info(
            "Finished Kinopoisk grab: genre={}, collected={}, returned={}",
            genre,
            collectedItems.size(),
            limitedItems.size()
        );

        return limitedItems;
    }

}
