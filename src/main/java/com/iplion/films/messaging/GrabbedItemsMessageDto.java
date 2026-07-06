package com.iplion.films.messaging;

import com.iplion.films.dto.FilmImportItemDto;

import java.util.List;

public record GrabbedItemsMessageDto(
    int version,
    List<FilmImportItemDto> items
) {
    public static final int VERSION = 1;

    public GrabbedItemsMessageDto(List<FilmImportItemDto> items) {
        this(VERSION, items);
    }
}
