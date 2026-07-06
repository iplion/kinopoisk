package com.iplion.films.service;

import com.iplion.films.dto.GrabFilmsRequestDto;
import com.iplion.films.dto.GrabFilmsResponseDto;
import com.iplion.films.integration.kinopoisk.KinopoiskClient;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskItemDto;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskItemsResponseDto;
import com.iplion.films.mapper.FilmImportItemMapper;
import com.iplion.films.mapper.KinopoiskItemsRequestDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        KinopoiskItemsResponseDto kinopoiskResponse = getKinopoiskItems(request);

        List<KinopoiskItemDto> items = Objects.requireNonNullElse(kinopoiskResponse.items(), List.of());

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

    public KinopoiskItemsResponseDto getKinopoiskItems(GrabFilmsRequestDto request) {
        return kinopoiskClient.getItems(
            kinopoiskItemsRequestDtoMapper.toKinopoiskItemsRequest(request)
        );
    }

}
