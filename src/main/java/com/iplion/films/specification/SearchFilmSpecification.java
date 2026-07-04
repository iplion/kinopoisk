package com.iplion.films.specification;

import com.iplion.films.dto.SearchFilmsRequestDto;
import com.iplion.films.entity.Film;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class SearchFilmSpecification {
    private SearchFilmSpecification() {}

    public static Specification<Film> byRequest(SearchFilmsRequestDto request) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (request.ratingFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), request.ratingFrom()));
            }

            if (request.ratingTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("rating"), request.ratingTo()));
            }

            if (request.yearFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("year"), request.yearFrom()));
            }

            if (request.yearTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("year"), request.yearTo()));
            }

            if (request.keyword() != null && !request.keyword().isBlank()) {
                String keyword = "%" + request.keyword().toLowerCase() + "%";

                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("filmName")), keyword),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), keyword)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

}
