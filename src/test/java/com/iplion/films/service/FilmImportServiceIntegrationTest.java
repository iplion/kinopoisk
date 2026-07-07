package com.iplion.films.service;

import com.iplion.films.dto.FilmImportItemDto;
import com.iplion.films.entity.Film;
import com.iplion.films.repository.FilmRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql(
    statements = "DELETE FROM films",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class FilmImportServiceIntegrationTest {
    @Autowired
    private FilmImportService filmImportService;

    @Autowired
    private FilmRepository filmRepository;

    @Sql(
        scripts = "/sql/insert-test-films.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Test
    public void processNewItems_shouldSaveFilmsNotAlreadyExistOnly() {
        var filmsBefore = filmRepository.findAll();

        int savedFilms = filmImportService.processNewItems(getFilmImportItemDtos());

        var filmsAfter = filmRepository.findAll();

        assertThat(savedFilms).isEqualTo(3);
        assertThat(filmsBefore).hasSize(3);
        assertThat(filmsAfter).hasSize(6);
        assertThat(filmsAfter)
            .extracting(Film::getFilmId)
            .containsExactlyInAnyOrder(1001L, 1002L, 1003L, 2001L, 2002L, 777L);

        Film existingFilm = findByFilmId(filmsAfter, 1001L);
        assertThat(existingFilm.getFilmName()).isEqualTo("The Matrix");
        assertThat(existingFilm.getYear()).isEqualTo(1999);
        assertThat(existingFilm.getRating()).isEqualByComparingTo("8.7");

        Film newFilm = findByFilmId(filmsAfter, 2001L);
        assertThat(newFilm.getFilmName()).isEqualTo("New film");
        assertThat(newFilm.getYear()).isEqualTo(2024);
        assertThat(newFilm.getRating()).isEqualByComparingTo("7.5");
        assertThat(newFilm.getDescription()).contains("Тип: FILM", "Страны: France", "Жанры: drama");

        Film anotherNewFilm = findByFilmId(filmsAfter, 2002L);
        assertThat(anotherNewFilm.getFilmName()).isEqualTo("Another new film / Another new film EN");
        assertThat(anotherNewFilm.getRating()).isEqualByComparingTo("8.1");

        Film strangeFilm = findByFilmId(filmsAfter, 777L);
        assertThat(strangeFilm.getFilmName()).isEqualTo("Really strange film");
        assertThat(strangeFilm.getYear()).isNull();
        assertThat(strangeFilm.getRating()).isNull();
        assertThat(strangeFilm.getDescription()).isEmpty();
    }

    private Film findByFilmId(List<Film> films, Long filmId) {
        return films.stream()
            .filter(film -> film.getFilmId().equals(filmId))
            .findFirst()
            .orElseThrow();
    }

    private List<FilmImportItemDto> getFilmImportItemDtos() {
        return List.of(
            new FilmImportItemDto(
                1001L,
                "Existing film from DB",
                null,
                null,
                List.of("USA"),
                List.of("sci-fi"),
                BigDecimal.valueOf(8.7),
                null,
                1999,
                "FILM",
                "https://example.com/existing.jpg"
            ),
            new FilmImportItemDto(
                2001L,
                "New film",
                null,
                null,
                List.of("France"),
                List.of("drama"),
                BigDecimal.valueOf(7.5),
                null,
                2024,
                "FILM",
                "https://example.com/new-film.jpg"
            ),
            new FilmImportItemDto(
                2001L,
                "Duplicate new film",
                null,
                null,
                List.of("France"),
                List.of("drama"),
                BigDecimal.valueOf(7.5),
                null,
                2024,
                "FILM",
                "https://example.com/duplicate-new-film.jpg"
            ),
            new FilmImportItemDto(
                null,
                "Film without kinopoisk id",
                null,
                null,
                List.of("Italy"),
                List.of("comedy"),
                BigDecimal.valueOf(6.9),
                null,
                2023,
                "FILM",
                "https://example.com/without-id.jpg"
            ),
            new FilmImportItemDto(
                2002L,
                "Another new film",
                "Another new film EN",
                null,
                List.of("Japan"),
                List.of("animation"),
                null,
                BigDecimal.valueOf(8.1),
                2022,
                "FILM",
                "https://example.com/another-new-film.jpg"
            ),
            new FilmImportItemDto(
                777L,
                null,
                null,
                "Really strange film",
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        );
    }
}
