package com.iplion.films.dto;

import java.math.BigDecimal;

public record FilmDto(
    Long filmId,
    String filmName,
    Integer year,
    BigDecimal rating,
    String description
) {
}
