package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.CreateTravelPackageDTO;
import com.orvian.travelapi.controller.dto.PackageSearchResultDTO;
import com.orvian.travelapi.domain.model.TravelPackage;
import com.orvian.travelapi.mapper.TravelPackageMapper;
import com.orvian.travelapi.service.exception.NoPackageFoundException;
import com.orvian.travelapi.service.impl.PackageServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/packages")
@RequiredArgsConstructor
@Slf4j
public class TravelPackageControllerImpl implements GenericController {
    private final PackageServiceImpl packageService;
    private final TravelPackageMapper travelPackageMapper;

    @GetMapping
    public ResponseEntity<?> getAllPackages() {
        try{
            log.info("Fetching all packages");
            List<PackageSearchResultDTO> packages = packageService.findAll();
            log.info("Total Packages found: {}", packages.size());
            return ResponseEntity.ok(packages);
        } catch (NoPackageFoundException e){
            log.info("No packages found");
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<TravelPackage> createPackage(@Valid @RequestBody CreateTravelPackageDTO dto) {
        log.info("Creating new travel package with details: {}", dto);
        try {
            var createdPackage = packageService.create(dto);
            log.info("Travel package created successfully with ID: {}", createdPackage.getId());
            return ResponseEntity.status(201).build();
        } catch (Exception e) {
            log.error("Error creating travel package: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
