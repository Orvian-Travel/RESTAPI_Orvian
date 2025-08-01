package com.orvian.travelapi.specs;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import com.orvian.travelapi.domain.model.PackageDate;
import com.orvian.travelapi.domain.model.TravelPackage;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class TravelPackageSpecs {

    public static Specification<TravelPackage> titleLike(String title) {
        return (root, query, cb) -> cb.like(cb.upper(root.get("title")), "%" + title.toUpperCase() + "%");
    }

    public static Specification<TravelPackage> hasStartDateFrom(LocalDate startDate) {
        return (Root<TravelPackage> root, @NonNull CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<PackageDate> packageDateRoot = subquery.from(PackageDate.class);
            subquery.select(packageDateRoot.get("travelPackage").get("id"))
                    .where(cb.greaterThanOrEqualTo(packageDateRoot.get("startDate"), startDate));

            return root.get("id").in(subquery);
        };
    }

    public static Specification<TravelPackage> maxPeopleGreaterThanOrEqual(Integer maxPeople) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("maxPeople"), maxPeople);
    }
}
