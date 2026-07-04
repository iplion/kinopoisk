package com.iplion.films.controller;

import com.iplion.films.dto.GrabFilmsRequestDto;
import com.iplion.films.dto.GrabFilmsResponseDto;
import com.iplion.films.service.KinopoiskGrabService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v2/kinopoisk")
@RequiredArgsConstructor
public class KinopoiskController {
    private final KinopoiskGrabService kinopoiskGrabService;

    @GetMapping("/grab")
    public GrabFilmsResponseDto grabFilms(
        @Valid @ModelAttribute GrabFilmsRequestDto request
    ) {
        return kinopoiskGrabService.grabFilms(request);
    }
}
