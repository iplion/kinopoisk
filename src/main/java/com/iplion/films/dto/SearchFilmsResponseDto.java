package com.iplion.films.dto;

import java.util.List;

public record SearchFilmsResponseDto(
    Long total,
    Integer totalPages,
    List<FilmDto> items
) {
}
