package com.orvian.travelapi.specs;

import com.orvian.travelapi.domain.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecs {

    public static Specification<User> nameLike(String name) {
        return (root, query, cb) -> cb.like(cb.upper(root.get("name")), "%" + name.toUpperCase() + "%");
    }
}
