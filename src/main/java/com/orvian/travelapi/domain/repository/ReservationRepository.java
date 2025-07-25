package com.orvian.travelapi.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.orvian.travelapi.domain.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @EntityGraph(attributePaths = {"travelers"})
    List<Reservation> findAll();

    @EntityGraph(attributePaths = {"travelers"})
    Optional<Reservation> findById(UUID id);

    boolean existsByUserIdAndPackageDateId(UUID userId, UUID packageDateId);
}
