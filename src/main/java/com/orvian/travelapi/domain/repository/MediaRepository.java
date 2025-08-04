package com.orvian.travelapi.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.orvian.travelapi.domain.model.Media;

public interface MediaRepository extends JpaRepository<Media, UUID> {

    @Query("SELECT m FROM Media m WHERE m.travelPackage.id = :packageId ORDER BY m.createdAt ASC LIMIT 1")
    Optional<Media> findFirstByTravelPackage_IdOrderByCreatedAtAsc(@Param("packageId") UUID packageId);

    @Query("SELECT m FROM Media m WHERE m.travelPackage.id = :packageId ORDER BY m.createdAt ASC")
    List<Media> findByTravelPackage_IdOrderByCreatedAtAsc(@Param("packageId") UUID packageId);
}
