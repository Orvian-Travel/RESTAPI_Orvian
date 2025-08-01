package com.orvian.travelapi.specs;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.orvian.travelapi.domain.model.User;

public class UserSpecs {

    public static Specification<User> nameLike(String name) {
        return (root, query, cb) -> cb.like(cb.upper(root.get("name")), "%" + name.toUpperCase() + "%");
    }

    public static Specification<User> idEquals(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }
}
