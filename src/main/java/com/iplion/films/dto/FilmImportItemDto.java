package com.iplion.films.dto;

import java.math.BigDecimal;
import java.util.List;

public record FilmImportItemDto(
    Long kinopoiskId,
    String nameRu,
    String nameEn,
    String nameOriginal,
    List<String> countries,
    List<String> genres,
    BigDecimal ratingKinopoisk,
    BigDecimal ratingImdb,
    Integer year,
    String type,
    String posterUrl
) {
}
