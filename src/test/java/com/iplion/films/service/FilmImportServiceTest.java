package com.iplion.films.service;

import com.iplion.films.dto.FilmImportItemDto;
import com.iplion.films.entity.Film;
import com.iplion.films.mapper.FilmMapper;
import com.iplion.films.repository.FilmRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class FilmImportServiceTest {

    @Mock
    private FilmMapper filmMapper;

    @Mock
    private FilmRepository filmRepository;

    @InjectMocks
    private FilmImportService filmImportService;

    @Test
    void processNewItems_shouldSaveOnlyNewUniqueValidItems() {
        FilmImportItemDto existingItem = createItem(1001L, "Existing film");
        FilmImportItemDto newItem = createItem(2001L, "New film");
        FilmImportItemDto duplicateNewItem = createItem(2001L, "Duplicate new film");
        FilmImportItemDto itemWithoutKinopoiskId = createItem(null, "Film without kinopoisk id");
        FilmImportItemDto itemWithoutName = createItem(2002L, null);
        FilmImportItemDto anotherNewItem = createItem(2002L, "Another new film");

        Film newFilm = createFilm(newItem.kinopoiskId(), newItem.nameRu());
        Film anotherNewFilm = createFilm(anotherNewItem.kinopoiskId(), anotherNewItem.nameRu());

        when(filmRepository.findExistingFilmIds(any())).thenReturn(Set.of(existingItem.kinopoiskId()));
        when(filmMapper.toFilm(newItem)).thenReturn(newFilm);
        when(filmMapper.toFilm(anotherNewItem)).thenReturn(anotherNewFilm);

        int savedFilmsCount = filmImportService.processNewItems(List.of(
            existingItem,
            newItem,
            duplicateNewItem,
            itemWithoutKinopoiskId,
            itemWithoutName,
            anotherNewItem
        ));

        assertThat(savedFilmsCount).isEqualTo(2);

        verify(filmRepository).findExistingFilmIds(List.of(
            existingItem.kinopoiskId(), newItem.kinopoiskId(), anotherNewItem.kinopoiskId())
        );

        verify(filmMapper).toFilm(newItem);
        verify(filmMapper).toFilm(anotherNewItem);
        verify(filmMapper, never()).toFilm(existingItem);
        verify(filmMapper, never()).toFilm(duplicateNewItem);
        verify(filmMapper, never()).toFilm(itemWithoutKinopoiskId);
        verify(filmMapper, never()).toFilm(itemWithoutName);

        ArgumentCaptor<List<Film>> filmsCaptor = ArgumentCaptor.forClass(List.class);
        verify(filmRepository).saveAll(filmsCaptor.capture());
        assertThat(filmsCaptor.getValue())
            .containsExactlyInAnyOrder(newFilm, anotherNewFilm);
    }

    @Test
    void processNewItems_shouldNotRequestExistingIdsWhenAllItemsInvalid() {
        FilmImportItemDto firstInvalidItem = createItem(null, "First invalid film");
        FilmImportItemDto secondInvalidItem = createItem(555L, null);
        FilmImportItemDto thirdInvalidItem = createItem(null, null);

        int savedFilmsCount = filmImportService.processNewItems(
            List.of(firstInvalidItem, secondInvalidItem, thirdInvalidItem)
        );

        assertThat(savedFilmsCount).isZero();

        verify(filmRepository, never()).findExistingFilmIds(any());
    }

    @Test
    void processNewItems_shouldNotSaveWhenAllValidItemsAlreadyExist() {
        FilmImportItemDto existingItem = createItem(1001L, "Existing film");
        FilmImportItemDto anotherExistingItem = createItem(1002L, "Another existing film");

        when(filmRepository.findExistingFilmIds(any()))
            .thenReturn(Set.of(existingItem.kinopoiskId(), anotherExistingItem.kinopoiskId()));

        int savedFilmsCount = filmImportService.processNewItems(List.of(existingItem, anotherExistingItem));

        assertThat(savedFilmsCount).isZero();

        verify(filmMapper, never()).toFilm(any());
        verify(filmRepository, never()).saveAll(any());
    }

    @Test
    void processNewItems_shouldNotTreatInvalidItemAsDuplicateForNextValidItem() {
        FilmImportItemDto invalidItem = createItem(2001L, null);
        FilmImportItemDto validItemWithSameId = createItem(2001L, "Valid film");
        Film validFilm = createFilm(validItemWithSameId.kinopoiskId(), validItemWithSameId.nameRu());

        when(filmRepository.findExistingFilmIds(any())).thenReturn(Set.of());
        when(filmMapper.toFilm(validItemWithSameId)).thenReturn(validFilm);

        int savedFilmsCount = filmImportService.processNewItems(List.of(invalidItem, validItemWithSameId));

        assertThat(savedFilmsCount).isEqualTo(1);

        verify(filmRepository).findExistingFilmIds(List.of(validFilm.getFilmId()));
        verify(filmMapper, never()).toFilm(invalidItem);
        verify(filmMapper).toFilm(validItemWithSameId);

        ArgumentCaptor<Iterable<Film>> filmsCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(filmRepository).saveAll(filmsCaptor.capture());
        assertThat(filmsCaptor.getValue())
            .containsExactly(validFilm);
    }

    // -------------------- helpers ------------------------

    private FilmImportItemDto createItem(Long kinopoiskId, String nameRu) {
        return new FilmImportItemDto(
            kinopoiskId,
            nameRu,
            null, null, null, null, null, null, null,
            null, null
        );
    }

    private Film createFilm(Long filmId, String filmName) {
        Film film = new Film();
        film.setFilmId(filmId);
        film.setFilmName(filmName);

        return film;
    }
}
