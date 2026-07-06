package com.iplion.films.dto;

import com.iplion.films.model.KinopoiskFilmType;
import com.iplion.films.model.KinopoiskSearchOrder;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record GrabFilmsRequestDto(
    @Positive
    Integer country,

    @Positive
    Integer genre,

    KinopoiskSearchOrder order,

    KinopoiskFilmType type,

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

    @Pattern(regexp = "^tt\\d+$", message = "imdbId must match format tt0133093")
    String imdbId,

    @Size(max = 255, message = "Search query must not be longer than 255 characters")
    String keyword,

    @Positive
    Integer page
) {
}
