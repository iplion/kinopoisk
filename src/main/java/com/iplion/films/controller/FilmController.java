package com.iplion.films.controller;

import com.iplion.films.dto.SearchFilmsRequestDto;
import com.iplion.films.dto.SearchFilmsResponseDto;
import com.iplion.films.service.FilmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public SearchFilmsResponseDto getFilms(
        @Valid @ModelAttribute SearchFilmsRequestDto request
    ) {
        return filmService.searchFilms(request);
    }
}
