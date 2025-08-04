package com.orvian.travelapi.domain.repository;

import com.orvian.travelapi.controller.dto.admin.WeekRatingDTO;
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

    @Query(value = "WITH medias AS (\n" +
            "  SELECT\n" +
            "    (SELECT AVG(RATE) FROM TB_RATINGS WHERE CREATED_AT >= DATEADD(DAY, -7, GETDATE())) AS atual,\n" +
            "    (SELECT AVG(RATE) FROM TB_RATINGS WHERE CREATED_AT >= DATEADD(DAY, -14, GETDATE()) AND CREATED_AT < DATEADD(DAY, -7, GETDATE())) AS anterior\n" +
            ")\n" +
            "SELECT\n" +
            "  atual,\n" +
            "  anterior,\n" +
            "  CASE \n" +
            "    WHEN anterior = 0 THEN NULL\n" +
            "    ELSE ((atual - anterior) / anterior) * 100\n" +
            "  END AS variacao_percentual\n" +
            "FROM medias;", nativeQuery = true)
    WeekRatingDTO ratingAVGThisWeek();
}