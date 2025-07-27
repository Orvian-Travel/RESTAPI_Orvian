package com.orvian.travelapi.specs;

import org.springframework.data.jpa.domain.Specification;

import com.orvian.travelapi.domain.model.TravelPackage;

public class TravelPackageSpecs {

    public static Specification<TravelPackage> titleLike(String title) {
        return (root, query, cb) -> cb.like(cb.upper(root.get("title")), "%" + title.toUpperCase() + "%");
    }
}
