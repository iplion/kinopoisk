package com.iplion.films.repository;

import com.iplion.films.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface FilmRepository  extends JpaRepository<Film, Long> {
    @Query("""
        select f.filmId
        from Film f
        where f.filmId in :filmIds
        """)
    Set<Long> findExistingFilmIds(Collection<Long> filmIds);

}
