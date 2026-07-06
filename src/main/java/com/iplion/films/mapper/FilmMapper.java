package com.iplion.films.mapper;

import com.iplion.films.dto.FilmImportItemDto;
import com.iplion.films.entity.Film;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FilmMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "filmId", source = "kinopoiskId")
    @Mapping(target = "filmName", source = "item", qualifiedByName = "resolveFilmName")
    @Mapping(target = "rating", source = "item", qualifiedByName = "resolveRating")
    @Mapping(target = "description", source = "item", qualifiedByName = "resolveDescription")
    Film toFilm(FilmImportItemDto item);

    @Named("resolveFilmName")
    default String resolveFilmName(FilmImportItemDto item) {
        String filmName = joinNotBlank(" / ", item.nameRu(), item.nameEn(), item.nameOriginal());

        if (!hasText(filmName)) {
            throw new IllegalArgumentException("Film name is missing for kinopoiskId=" + item.kinopoiskId());
        }

        return filmName;
    }

    @Named("resolveRating")
    default BigDecimal resolveRating(FilmImportItemDto item) {
        if (item.ratingKinopoisk() != null) {
            return item.ratingKinopoisk();
        }

        return item.ratingImdb();
    }

    @Named("resolveDescription")
    default String resolveDescription(FilmImportItemDto item) {
        String countries = item.countries() == null
            ? null
            : item.countries().stream()
            .filter(this::hasText)
            .collect(Collectors.joining(", "));

        String genres = item.genres() == null
            ? null
            : item.genres().stream()
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
