package com.orvian.travelapi.domain.repository;

import com.orvian.travelapi.controller.dto.admin.WeekRatingDTO;
import com.orvian.travelapi.domain.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
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
