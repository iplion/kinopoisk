package com.iplion.films.mapper;

import com.iplion.films.dto.FilmImportItemDto;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskCountryDto;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskGenreDto;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface FilmImportItemMapper {

    @Mapping(target = "countries", source = "countries", qualifiedByName = "mapCountries")
    @Mapping(target = "genres", source = "genres", qualifiedByName = "mapGenres")
    FilmImportItemDto toFilmImportItemDto(KinopoiskItemDto item);

    @Named("mapCountries")
    default List<String> mapCountries(List<KinopoiskCountryDto> countries) {
        return mapValues(countries, KinopoiskCountryDto::country);
    }

    @Named("mapGenres")
    default List<String> mapGenres(List<KinopoiskGenreDto> genres) {
        return mapValues(genres, KinopoiskGenreDto::genre);
    }

    default <T> List<String> mapValues(List<T> values, Function<T, String> extractor) {
        if (values == null) {
            return List.of();
        }

        return values.stream()
            .map(extractor)
            .filter(value -> value != null && !value.isBlank())
            .toList();
    }

}
