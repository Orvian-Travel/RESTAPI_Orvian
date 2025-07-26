package com.orvian.travelapi.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orvian.travelapi.domain.model.PackageDate;

public interface PackageDateRepository extends JpaRepository<PackageDate, UUID> {

    List<PackageDate> findByTravelPackage_Id(UUID travelPackageId);

}
