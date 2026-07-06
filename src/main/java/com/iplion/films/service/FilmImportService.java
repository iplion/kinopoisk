package com.iplion.films.service;

import com.iplion.films.dto.FilmImportItemDto;
import com.iplion.films.entity.Film;
import com.iplion.films.mapper.FilmMapper;
import com.iplion.films.repository.FilmRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmImportService {
    private final FilmMapper filmMapper;
    private final FilmRepository filmRepository;

    // returns films saved count
    @Transactional
    public int processNewItems(@NotNull List<FilmImportItemDto> items) {
        var filteredItems = filterNewItems(items);
        saveFilms(filteredItems);

        return filteredItems.size();
    }

    private List<FilmImportItemDto> filterNewItems(List<FilmImportItemDto> items) {
        Set<Long> uniqueFilmIds = new HashSet<>();

        List<FilmImportItemDto> validKinopoiskItems = items.stream()
            .filter(item -> item.kinopoiskId() != null)
            .filter(item -> uniqueFilmIds.add(item.kinopoiskId()))
            .toList();

        if (items.size() != validKinopoiskItems.size()) {
            log.warn(
                "Received {} items from Kinopoisk, but only {} items valid",
                items.size(),
                validKinopoiskItems.size()
            );
        }

        List<Long> grabbedFilmIds = validKinopoiskItems.stream()
            .map(FilmImportItemDto::kinopoiskId)
            .toList();

        Set<Long> existingFilmIdsFromDb = grabbedFilmIds.isEmpty()
            ? Set.of()
            : filmRepository.findExistingFilmIds(grabbedFilmIds);

        return validKinopoiskItems.stream()
            .filter(item -> !existingFilmIdsFromDb.contains(item.kinopoiskId()))
            .toList();
    }

    private void saveFilms(List<FilmImportItemDto> items) {
        List<Film> newFilms = items.stream()
            .map(filmMapper::toFilm)
            .toList();

        filmRepository.saveAll(newFilms);
    }
}
