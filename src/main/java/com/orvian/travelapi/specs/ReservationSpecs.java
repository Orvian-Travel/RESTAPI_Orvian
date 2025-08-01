package com.orvian.travelapi.specs;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.orvian.travelapi.domain.enums.ReservationSituation;
import com.orvian.travelapi.domain.model.Reservation;

import jakarta.persistence.criteria.Predicate;

public class ReservationSpecs {

    public static Specification<Reservation> idEquals(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    public static Specification<Reservation> userIdEquals(UUID userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Reservation> situationEquals(ReservationSituation situation) {
        return (root, query, criteriaBuilder)
                -> situation == null ? null : criteriaBuilder.equal(root.get("situation"), situation);
    }

    public static Specification<Reservation> userIdAndSituation(UUID userId, ReservationSituation situation) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }

            if (situation != null) {
                predicates.add(criteriaBuilder.equal(root.get("situation"), situation));
            }
            if (predicates.isEmpty()) {
                return null;
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
