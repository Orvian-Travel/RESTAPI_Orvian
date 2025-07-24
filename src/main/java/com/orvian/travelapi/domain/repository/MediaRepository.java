package com.orvian.travelapi.domain.repository;

import com.orvian.travelapi.domain.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaRepository extends JpaRepository<Media, UUID> {
}
