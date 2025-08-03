package com.orvian.travelapi.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.orvian.travelapi.domain.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @EntityGraph(attributePaths = {"travelers"})
    Page<Reservation> findAll(Specification<Reservation> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"travelers"})
    Optional<Reservation> findById(UUID id);

    boolean existsByUserIdAndPackageDateId(UUID userId, UUID packageDateId);

    @Query("""
        SELECT DISTINCT r.reservationDate 
        FROM Reservation r 
        WHERE r.user.id = :userId 
        ORDER BY r.reservationDate DESC
        """)
    List<LocalDate> findDistinctReservationDatesByUserId(@Param("userId") UUID userId);

    @Query("""
        SELECT DISTINCT r.reservationDate 
        FROM Reservation r 
        ORDER BY r.reservationDate DESC
        """)
    List<LocalDate> findAllDistinctReservationDates();

}
