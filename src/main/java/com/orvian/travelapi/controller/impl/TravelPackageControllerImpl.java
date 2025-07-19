package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.error.ResponseErrorDTO;
import com.orvian.travelapi.controller.dto.travelpackage.CreateTravelPackageDTO;
import com.orvian.travelapi.controller.dto.travelpackage.PackageSearchResultDTO;
import com.orvian.travelapi.controller.dto.travelpackage.UpdateTravelPackageDTO;
import com.orvian.travelapi.domain.model.TravelPackage;
import com.orvian.travelapi.mapper.TravelPackageMapper;
import com.orvian.travelapi.service.exception.NotFoundException;
import com.orvian.travelapi.service.impl.PackageServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
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
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<List<PackageSearchResultDTO>> getAllPackages() {
        log.info("Fetching all packages");
        List<PackageSearchResultDTO> packages = packageService.findAll();
        log.info("Total Packages found: {}", packages.size());
        return ResponseEntity.ok(packages);
    }

    @PostMapping
    @Operation(summary = "Create a new travel package", description = "Creates a new travel package with the provided details.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Travel package created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Package with the same data already exists", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> createPackage(@Valid @RequestBody CreateTravelPackageDTO dto) {
        log.info("Creating new travel package with details: {}", dto);
        TravelPackage createdPackage = packageService.create(dto);
        log.info("Travel package created successfully with ID: {}", createdPackage.getId());
        URI location = generateHeaderLocation(createdPackage.getId());
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a travel package", description = "Deletes a travel package identified by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Travel package deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Travel package not found", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> deletePackage(@PathVariable UUID id) {
        log.info("Deleting travel package with ID: {}", id);
        packageService.delete(id);
        log.info("Travel package deleted successfully");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a travel package", description = "Updates the details of an existing travel package identified by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Package updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Package not found", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Package with the same data already exists", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Object> updatePackage(@PathVariable UUID id, @Valid @RequestBody UpdateTravelPackageDTO dto) {
        log.info("Updating Package with id: {}", id);
        packageService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a travel package by ID", description = "Fetches a travel package identified by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Travel package found"),
            @ApiResponse(responseCode = "404", description = "Travel package not found", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<PackageSearchResultDTO> getPackageById(@PathVariable UUID id) {
        return packageService.findById(id)
                .map(travelPackage -> {
                    PackageSearchResultDTO dto = travelPackageMapper.toPackageSearchResultDTO(travelPackage);
                    log.info("Travel package found with ID: {}", id);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet( () -> {
                    log.error("Travel package not found with ID: {}", id);
                    throw new NotFoundException("Travel package not found with ID: " + id);
                });
    }
}