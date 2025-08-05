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
import org.springframework.lang.NonNull;

import com.orvian.travelapi.controller.dto.admin.ReservationToSheetDTO;
import com.orvian.travelapi.domain.enums.ReservationSituation;
import com.orvian.travelapi.domain.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @EntityGraph(attributePaths = {"travelers"})
    Page<Reservation> findAll(Specification<Reservation> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"travelers"})
    @NonNull
    Optional<Reservation> findById(@NonNull UUID id);

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

    @Query(value = "SELECT * FROM VW_RESERVATIONS_WITH_PACKAGE", nativeQuery = true)
    List<ReservationToSheetDTO> exportToSheet();

    /**
     * Verifica se existem reservas para um pacote de viagem que não estejam no
     * status especificado
     *
     * @param travelPackageId ID do pacote de viagem
     * @param status Status da reserva a ser excluído da busca
     * @return true se existirem reservas ativas (não no status especificado)
     */
    boolean existsByPackageDate_TravelPackage_IdAndSituationNot(UUID travelPackageId, ReservationSituation status);

}
