package com.orvian.travelapi.specs;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.orvian.travelapi.domain.model.Reservation;

public class ReservationSpecs {

    public static Specification<Reservation> idEquals(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    public static Specification<Reservation> userIdEquals(UUID userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }
}
