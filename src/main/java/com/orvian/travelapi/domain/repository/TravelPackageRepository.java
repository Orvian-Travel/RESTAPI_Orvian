package com.orvian.travelapi.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orvian.travelapi.domain.model.TravelPackage;

public interface TravelPackageRepository extends JpaRepository<TravelPackage, UUID> {

    Optional<TravelPackage> findByTitle(String title);

}
