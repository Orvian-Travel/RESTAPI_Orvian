package com.orvian.travelapi.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.orvian.travelapi.controller.dto.travelpackage.PaymentByPackageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.orvian.travelapi.domain.model.TravelPackage;
import org.springframework.data.jpa.repository.Query;

public interface TravelPackageRepository extends JpaRepository<TravelPackage, UUID> {

    Optional<TravelPackage> findByTitle(String title);

    Page<TravelPackage> findAll(Specification<TravelPackage> spec, Pageable pageable);

    @Query(value = "SELECT * FROM VW_CONFIRMED_RESERVATIONS_SUM_PAYMENTS_BY_DESTINATION", nativeQuery = true)
    List<PaymentByPackageDTO> sumTotalByPackage();
}
