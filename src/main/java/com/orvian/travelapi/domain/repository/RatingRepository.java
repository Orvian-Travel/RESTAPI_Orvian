package com.orvian.travelapi.domain.repository;

import com.orvian.travelapi.domain.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
}
