package com.orvian.travelapi.domain.repository;

import com.orvian.travelapi.domain.model.PackageDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PackageDateRepository extends JpaRepository <PackageDate, UUID> {

}
