package com.iplion.films.service;

import com.iplion.films.dto.SearchFilmsRequestDto;
import com.iplion.films.dto.SearchFilmsResponseDto;
import com.iplion.films.entity.Film;
import com.iplion.films.mapper.FilmToFilmDtoMapper;
import com.iplion.films.model.FilmSearchOrder;
import com.iplion.films.repository.FilmRepository;
import com.iplion.films.specification.SearchFilmSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final FilmRepository filmRepository;

    private final FilmToFilmDtoMapper filmToFilmDtoMapper;

    public SearchFilmsResponseDto searchFilms(SearchFilmsRequestDto request) {
        Pageable pageable = createPageable(request.page(), request.size(), request.order());

        Page<Film> films = filmRepository.findAll(SearchFilmSpecification.byRequest(request), pageable);

        return new SearchFilmsResponseDto(
            films.getTotalElements(),
            films.getTotalPages(),
            films.getContent().stream()
                .map(filmToFilmDtoMapper::toFilmDto)
                .toList()
        );
    }

    private Pageable createPageable(Integer page, Integer size, FilmSearchOrder order) {
        int resultPage = page == null ? 0 : page;
        int resultSize = size == null ? DEFAULT_PAGE_SIZE : size;

        Sort sort = order == null
            ? Sort.by(Sort.Direction.DESC, FilmSearchOrder.RATING.getValue())
            : Sort.by(order.getValue());

        return PageRequest.of(
            resultPage,
            resultSize,
            sort
        );
    }

}
