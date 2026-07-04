package com.iplion.films.integration.kinopoisk.dto;

import java.math.BigDecimal;
import java.util.List;

public record KinopoiskItemDto(
    Long kinopoiskId,
    String nameRu,
    String nameEn,
    String nameOriginal,
    List<KinopoiskCountryDto> countries,
    List<KinopoiskGenreDto> genres,
    BigDecimal ratingKinopoisk,
    BigDecimal ratingImdb,
    Integer year,
    String type,
    String posterUrl,
    String posterUrlPreview
) {
}
