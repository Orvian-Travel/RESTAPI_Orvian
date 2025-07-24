package com.orvian.travelapi.domain.repository;

import com.orvian.travelapi.domain.model.Reservation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    @EntityGraph(attributePaths = {"travelers", "payment"})
    List<Reservation> findAll();

    @EntityGraph(attributePaths = {"travelers", "payment"})
    Optional<Reservation> findById(UUID id);

    boolean existsByUserIdAndPackageDateId(UUID userId, UUID packageDateId);
}
