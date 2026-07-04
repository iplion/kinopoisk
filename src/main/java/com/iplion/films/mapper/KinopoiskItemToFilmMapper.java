package com.iplion.films.mapper;

import com.iplion.films.entity.Film;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskCountryDto;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskGenreDto;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface KinopoiskItemToFilmMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "filmId", source = "kinopoiskId")
    @Mapping(target = "filmName", source = "item", qualifiedByName = "resolveFilmName")
    @Mapping(target = "rating", source = "item", qualifiedByName = "resolveRating")
    @Mapping(target = "description", source = "item", qualifiedByName = "resolveDescription")
    Film toFilm(KinopoiskItemDto item);

    @Named("resolveFilmName")
    default String resolveFilmName(KinopoiskItemDto item) {
        String filmName = joinNotBlank(" / ", item.nameRu(), item.nameEn(), item.nameOriginal());

        if (!hasText(filmName)) {
            throw new IllegalArgumentException("Film name is missing for kinopoiskId=" + item.kinopoiskId());
        }

        return filmName;
    }

    @Named("resolveRating")
    default BigDecimal resolveRating(KinopoiskItemDto item) {
        if (item.ratingKinopoisk() != null) {
            return item.ratingKinopoisk();
        }

        return item.ratingImdb();
    }

    @Named("resolveDescription")
    default String resolveDescription(KinopoiskItemDto item) {
        String countries = item.countries() == null
            ? null
            : item.countries().stream()
            .map(KinopoiskCountryDto::country)
            .filter(this::hasText)
            .collect(Collectors.joining(", "));

        String genres = item.genres() == null
            ? null
            : item.genres().stream()
            .map(KinopoiskGenreDto::genre)
            .filter(this::hasText)
            .collect(Collectors.joining(", "));

        return joinNotBlank(
            "; ",
            label("Тип", item.type()),
            label("Страны", countries),
            label("Жанры", genres),
            label("Постер", item.posterUrl())
        );
    }

    default String joinNotBlank(String delimiter, String... lines) {
        return Arrays.stream(lines)
            .filter(this::hasText)
            .collect(Collectors.joining(delimiter));
    }

    default String label(String label, String value) {
        return hasText(value) ? label + ": " + value : null;
    }

    default boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
