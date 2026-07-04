package com.iplion.films.integration.kinopoisk.dto;

import com.iplion.films.model.KinopoiskFilmType;
import com.iplion.films.model.KinopoiskSearchOrder;

import java.math.BigDecimal;
import java.util.List;

public record KinopoiskItemsRequestDto(
    // список id стран разделенные запятой. Например countries=1,2,3. На данный момент можно указать не более одной страны.
    List<Integer> countries,

    // список id жанров разделенные запятой. Например genres=1,2,3. На данный момент можно указать не более одного жанра.
    List<Integer> genres,

    KinopoiskSearchOrder order,

    KinopoiskFilmType type,

    BigDecimal ratingFrom,

    BigDecimal ratingTo,

    Integer yearFrom,

    Integer yearTo,

    String imdbId,

    // ключевое слово, которое встречается в названии фильма
    String keyword,

    Integer page
) {
}
