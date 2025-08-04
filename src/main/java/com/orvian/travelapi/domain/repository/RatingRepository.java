package com.orvian.travelapi.domain.repository;

import com.orvian.travelapi.domain.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    boolean existsByReservationId(UUID reservationId);
    @Query("SELECT r FROM Rating r " +
            "JOIN FETCH r.reservation res " +
            "JOIN FETCH res.user " +
            "JOIN FETCH res.packageDate pd " +
            "JOIN FETCH pd.travelPackage " +
            "WHERE pd.travelPackage.id = :travelPackageId " +
            "ORDER BY r.createdAt DESC")
    List<Rating> findByTravelPackageIdOrderByCreatedAtDesc(@Param("travelPackageId") UUID travelPackageId);

    @Query("SELECT r FROM Rating r " +
            "JOIN FETCH r.reservation res " +
            "JOIN FETCH res.user " +
            "JOIN FETCH res.packageDate pd " +
            "JOIN FETCH pd.travelPackage")
    List<Rating> findAllWithDetails();
}