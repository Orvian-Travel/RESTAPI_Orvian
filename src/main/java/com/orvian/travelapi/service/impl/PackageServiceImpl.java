package com.orvian.travelapi.service.impl;

import com.orvian.travelapi.controller.dto.travelpackage.CreateTravelPackageDTO;
import com.orvian.travelapi.controller.dto.travelpackage.PackageSearchResultDTO;
import com.orvian.travelapi.controller.dto.travelpackage.UpdateTravelPackageDTO;
import com.orvian.travelapi.domain.model.TravelPackage;
import com.orvian.travelapi.domain.repository.TravelPackageRepository;
import com.orvian.travelapi.mapper.TravelPackageMapper;
import com.orvian.travelapi.service.TravelPackageService;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import com.orvian.travelapi.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PackageServiceImpl implements TravelPackageService {

    private final TravelPackageRepository travelPackageRepository;
    private final TravelPackageMapper travelPackageMapper;

    @Override
    public List<PackageSearchResultDTO> findAll() {
        List<TravelPackage> packages = travelPackageRepository.findAll();
        return travelPackageMapper.toPackageSearchResultDTOList(packages);
    }

    @Override
    public TravelPackage create(Record dto) {
        TravelPackage travelPackage = travelPackageMapper.toTravelPackage((CreateTravelPackageDTO) dto);
        validateCreationAndUpdate(travelPackage);
        return travelPackageRepository.save(travelPackage);
    }

    @Override
    public PackageSearchResultDTO findById(UUID id) {
        TravelPackage travelPackage = travelPackageRepository.findById(id).orElseThrow(() -> new NotFoundException("Travel package with ID " + id + " not found."));
        log.info("Travel package found with ID: {}", id);
        return travelPackageMapper.toPackageSearchResultDTO(travelPackage);
    }

    @Override
    public void update(UUID id, Record dto) {
        Optional<TravelPackage> packageOptional = travelPackageRepository.findById(id);
        if (packageOptional.isEmpty()) {
            log.error("Travel package not found with ID: {}", id);
            throw new NotFoundException("Travel package not found with ID: " + id);
        }

        TravelPackage travelPackage = packageOptional.get();
        log.info("Updating Package with ID: {}", travelPackage.getId());
        validateCreationAndUpdate(travelPackage);

        travelPackageMapper.updateEntityFromDto((UpdateTravelPackageDTO) dto, travelPackage);

        travelPackageRepository.save(travelPackage);
        log.info("Package with ID: {} updated successfully", travelPackage.getId());
    }

    @Override
    public void delete(UUID id) {
        Optional<TravelPackage> packageOptional = travelPackageRepository.findById(id);

        if (packageOptional.isEmpty()) {
            log.error("Travel package not found with ID: {}", id);
            throw new NotFoundException("Travel package with ID " + id + " not found.");
        }

        travelPackageRepository.deleteById(id);
        log.info("Travel package with ID: {} deleted successfully", id);
    }

    private void validateCreationAndUpdate(TravelPackage travelPackage) {
        if (isDuplicatePackage(travelPackage)) {
            throw new DuplicatedRegistryException("A travel package with the same data already exists.");
        }
    }

    private boolean isDuplicatePackage(TravelPackage travelPackage) {
        Optional<TravelPackage> packageOptional = travelPackageRepository.findByTitle(travelPackage.getTitle());

        if (travelPackage.getId() == null) {
            return packageOptional.isPresent();
        }

        return !travelPackage.getId().equals(packageOptional.get().getId()) && packageOptional.isPresent();
    }
}
