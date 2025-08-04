package com.orvian.travelapi.service;

import com.orvian.travelapi.controller.dto.rating.CreateRatingDTO;
import com.orvian.travelapi.controller.dto.rating.RatingDTO;

import java.util.List;
import java.util.UUID;

public interface RatingService {
    RatingDTO create(CreateRatingDTO dto, UUID userId);
    List<RatingDTO> findAll();
    List<RatingDTO> findByTravelPackage(UUID travelPackageId);
    RatingDTO findById(UUID id);
    void delete(UUID id);
}