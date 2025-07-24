package com.orvian.travelapi.domain.repository;

import com.orvian.travelapi.domain.model.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TravelPackageRepository extends JpaRepository<TravelPackage, UUID> {
    Optional<TravelPackage> findByTitle(String title);
}
