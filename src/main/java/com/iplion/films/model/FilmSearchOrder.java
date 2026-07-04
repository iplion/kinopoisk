package com.iplion.films.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FilmSearchOrder {
    RATING("rating"),
    FILM_NAME("filmName"),
    YEAR("year");

    private final String value;
}
