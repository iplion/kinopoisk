package com.iplion.films.mapper;

import com.iplion.films.dto.GrabFilmsRequestDto;
import com.iplion.films.integration.kinopoisk.dto.KinopoiskItemsRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrabRequestToKinopoiskRequestMapper {

    @Mapping(target = "countries", source = "country", qualifiedByName = "integerToList")
    @Mapping(target = "genres", source = "genre", qualifiedByName = "integerToList")
    KinopoiskItemsRequestDto toKinopoiskItemsRequest(GrabFilmsRequestDto request);

    @Named("integerToList")
    default List<Integer> integerToList(Integer value) {
        return value == null ? null : List.of(value);
    }
}
