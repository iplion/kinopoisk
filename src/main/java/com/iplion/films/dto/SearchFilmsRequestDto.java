package com.iplion.films.dto;

import com.iplion.films.model.FilmSearchOrder;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record SearchFilmsRequestDto(
    FilmSearchOrder order,

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    BigDecimal ratingFrom,

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    BigDecimal ratingTo,

    @Min(1888)
    @Max(3000)
    Integer yearFrom,

    @Min(1888)
    @Max(3000)
    Integer yearTo,

    @Size(max = 255, message = "Search query must not be longer than 255 characters")
    String keyword,

    @PositiveOrZero
    Integer page,

    @Min(1)
    @Max(100)
    Integer size
) {
}
