package com.iplion.films.dto;

public record GrabFilmsResponseDto(
    int received,
    int alreadyExists,
    int saved
) {
}
