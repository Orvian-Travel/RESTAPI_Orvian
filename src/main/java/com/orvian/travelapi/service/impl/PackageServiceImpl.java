package com.orvian.travelapi.service.impl;

import com.orvian.travelapi.controller.dto.travelpackage.CreateTravelPackageDTO;
import com.orvian.travelapi.controller.dto.travelpackage.PackageSearchResultDTO;
import com.orvian.travelapi.domain.model.TravelPackage;
import com.orvian.travelapi.domain.repository.TravelPackageRepository;
import com.orvian.travelapi.mapper.TravelPackageMapper;
import com.orvian.travelapi.service.TravelPackageService;
import com.orvian.travelapi.service.exception.NoPackageFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements TravelPackageService {
    private final TravelPackageRepository travelPackageRepository;
    private final TravelPackageMapper travelPackageMapper;

    public List<PackageSearchResultDTO> findAll() {
        List<TravelPackage> packages = travelPackageRepository.findAll();
        if(packages == null || packages.isEmpty()) {
            throw new NoPackageFoundException("No travel packages found.");
        }
        return travelPackageMapper.toPackageSearchResultDTOList(packages);
    }

    @Override
    public Optional<TravelPackage> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public TravelPackage create(TravelPackage entity) {
        return travelPackageRepository.save(entity);
    }

    public TravelPackage create(CreateTravelPackageDTO dto) {
        TravelPackage travelPackage = travelPackageMapper.toTravelPackage(dto);
        Date now = new Date();
        travelPackage.setCreatedAt(now);
        travelPackage.setUpdatedAt(now);
        return create(travelPackage);
    }

    @Override
    public TravelPackage update(TravelPackage entity) {
        if(entity.getId() == null || !travelPackageRepository.existsById(entity.getId())) {;
            throw new NoPackageFoundException("Travel package with ID " + entity.getId() + " not found.");
        }
        TravelPackage existingPackage = travelPackageRepository.findById(entity.getId())
                .orElseThrow(() -> new NoPackageFoundException("Travel package with ID " + entity.getId() + " not found."));
        entity.setCreatedAt(existingPackage.getCreatedAt());
        entity.setUpdatedAt(new Date());
        return travelPackageRepository.save(entity);
    }

    @Override
    public void delete(UUID uuid) {
        if(uuid.equals(null) || !travelPackageRepository.existsById(uuid)){
            throw new NoPackageFoundException("Travel package with ID " + uuid + " not found.");
        } else {
            travelPackageRepository.deleteById(uuid);
        }
    }
}
