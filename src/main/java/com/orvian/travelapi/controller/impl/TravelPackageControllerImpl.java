package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.PackageSearchResultDTO;
import com.orvian.travelapi.mapper.TravelPackageMapper;
import com.orvian.travelapi.service.exception.NoPackageFoundException;
import com.orvian.travelapi.service.impl.PackageServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
