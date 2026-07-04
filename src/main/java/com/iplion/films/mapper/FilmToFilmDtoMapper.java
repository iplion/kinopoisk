package com.iplion.films.mapper;

import com.iplion.films.dto.FilmDto;
import com.iplion.films.entity.Film;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FilmToFilmDtoMapper {
    FilmDto toFilmDto(Film film);
}
