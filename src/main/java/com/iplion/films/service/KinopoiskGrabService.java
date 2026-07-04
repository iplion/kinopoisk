package com.iplion.films.service;

import com.iplion.films.dto.GrabFilmsRequestDto;
import com.iplion.films.dto.GrabFilmsResponseDto;
import com.iplion.films.entity.Film;
import com.iplion.films.integration.kinopoisk.KinopoiskClient;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskItemDto;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskItemsResponseDto;
import com.iplion.films.mapper.GrabRequestToKinopoiskRequestMapper;
import com.iplion.films.mapper.KinopoiskItemToFilmMapper;
import com.iplion.films.repository.FilmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class KinopoiskGrabService {

    private final KinopoiskClient kinopoiskClient;
    private final GrabRequestToKinopoiskRequestMapper grabRequestToKinopoiskRequestMapper;
    private final KinopoiskItemToFilmMapper kinopoiskItemToFilmMapper;
    private final FilmRepository filmRepository;

    @Transactional
    public GrabFilmsResponseDto grabFilms(GrabFilmsRequestDto request) {
        KinopoiskItemsResponseDto kinopoiskResponse = kinopoiskClient.getFilmsList(
            grabRequestToKinopoiskRequestMapper.toKinopoiskItemsRequest(request)
        );

        List<KinopoiskItemDto> kinopoiskItems = Objects.requireNonNullElse(
            kinopoiskResponse.items(),
            List.of()
        );

        Set<Long> uniqueFilmIds = new HashSet<>();

        List<KinopoiskItemDto> validKinopoiskItems = kinopoiskItems.stream()
            .filter(item -> item.kinopoiskId() != null)
            .filter(item -> uniqueFilmIds.add(item.kinopoiskId()))
            .toList();

        if (kinopoiskItems.size() != validKinopoiskItems.size()) {
            log.warn(
                "Received {} items from Kinopoisk, but only {} items valid",
                kinopoiskItems.size(),
                validKinopoiskItems.size()
            );
        }

        List<Long> grabbedFilmIds = validKinopoiskItems.stream()
            .map(KinopoiskItemDto::kinopoiskId)
            .toList();

        Set<Long> existingFilmIdsFromDb = grabbedFilmIds.isEmpty()
            ? Set.of()
            : filmRepository.findExistingFilmIds(grabbedFilmIds);

        List<Film> newFilms = validKinopoiskItems.stream()
            .filter(item -> !existingFilmIdsFromDb.contains(item.kinopoiskId()))
            .map(kinopoiskItemToFilmMapper::toFilm)
            .toList();

        filmRepository.saveAll(newFilms);

        return new GrabFilmsResponseDto(
            kinopoiskItems.size(),
            existingFilmIdsFromDb.size(),
            newFilms.size()
        );
    }
}
