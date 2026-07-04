package com.iplion.films.integration.kinopoisk.dto;

import java.util.List;

public record KinopoiskItemsResponseDto(
    Integer total,
    Integer totalPages,
    List<KinopoiskItemDto> items
) {
}
