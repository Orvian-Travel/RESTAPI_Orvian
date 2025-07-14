package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.travelpackage.CreateTravelPackageDTO;
import com.orvian.travelapi.controller.dto.travelpackage.PackageSearchResultDTO;
import com.orvian.travelapi.controller.dto.travelpackage.UpdateTravelPackageDTO;
import com.orvian.travelapi.domain.model.TravelPackage;
import com.orvian.travelapi.mapper.TravelPackageMapper;
import com.orvian.travelapi.service.exception.NoPackageFoundException;
import com.orvian.travelapi.service.impl.PackageServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.PostUpdate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/packages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Package Management", description = "Operations related to travel package management")
public class TravelPackageControllerImpl implements GenericController {
    private final PackageServiceImpl packageService;
    private final TravelPackageMapper travelPackageMapper;

    @GetMapping
    @Operation(summary = "Get all travel packages", description = "Fetches a list of all available travel packages.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all travel packages"),
            @ApiResponse(responseCode = "404", description = "No travel packages found")
    })
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
    @Operation(summary = "Create a new travel package", description = "Creates a new travel package with the provided details.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Travel package created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a travel package", description = "Deletes a travel package identified by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Travel package deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Travel package not found", content = @Content)
    })
    public ResponseEntity<?> deletePackage(@PathVariable UUID id) {
        try {
            log.info("Deleting travel package with ID: {}", id);
            packageService.delete(id);
            log.info("Travel package deleted successfully");
            return ResponseEntity.noContent().build();
        } catch (NoPackageFoundException e) {
            log.info("Error deleting travel package");
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a travel package", description = "Updates the details of an existing travel package identified by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Travel package updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Travel package not found", content = @Content)
    })
    public ResponseEntity<?> updatePackage(@PathVariable UUID id, @Valid @RequestBody UpdateTravelPackageDTO dto) {
        log.info("Updating travel package with ID: {}", id);
        try {
            TravelPackage travelPackage = travelPackageMapper.toUpdateTravelPackage(dto);
            travelPackage.setId(id);
            var updatedPackage = packageService.update(travelPackage);
            log.info("Travel package updated successfully with ID: {}", updatedPackage.getId());
            return ResponseEntity.ok(updatedPackage);
        } catch (NoPackageFoundException e) {
            log.info("Error updating travel package: {}", e.getMessage());
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a travel package by ID", description = "Fetches a travel package identified by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Travel package found"),
            @ApiResponse(responseCode = "404", description = "Travel package not found", content = @Content)
    })
    public ResponseEntity<?> getPackageById(@PathVariable UUID id) {
        log.info("Fetching travel package with ID: {}", id);
        try {
            var travelPackage = packageService.findById(id)
                    .orElseThrow(() -> new NoPackageFoundException("Travel package with ID " + id + " not found."));
            log.info("Travel package found with ID: {}", travelPackage.getId());
            return ResponseEntity.ok(travelPackage);
        } catch (NoPackageFoundException e) {
            log.info("Error fetching travel package: {}", e.getMessage());
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
