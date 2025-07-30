package com.orvian.travelapi.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orvian.travelapi.controller.dto.packagedate.CreatePackageDateDTO;
import com.orvian.travelapi.controller.dto.packagedate.UpdatePackageDateDTO;
import com.orvian.travelapi.domain.model.PackageDate;
import com.orvian.travelapi.domain.repository.PackageDateRepository;
import com.orvian.travelapi.service.PackageDateService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PackageDateServiceImpl implements PackageDateService {

    @Autowired
    private PackageDateRepository packageDateRepository;

    @Override
    public List<PackageDate> findAll() {
        return packageDateRepository.findAll();
    }

    @Override
    public PackageDate create(Record dto) {
        CreatePackageDateDTO createDto = (CreatePackageDateDTO) dto;

        PackageDate packageDate = new PackageDate();
        packageDate.setStartDate(createDto.startDate());
        packageDate.setEndDate(createDto.endDate());
        packageDate.setQtd_available(createDto.qtd_available());

        // Buscar o TravelPackage pelo ID
        // TravelPackage travelPackage = travelPackageRepository.findById(createDto.travelPackageId())
        //         .orElseThrow(() -> new EntityNotFoundException("TravelPackage não encontrado"));
        // packageDate.setTravelPackage(travelPackage);
        return packageDateRepository.save(packageDate);
    }

    @Override
    public PackageDate findById(UUID id) {
        return packageDateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PackageDate não encontrado"));
    }

    @Override
    public void update(UUID id, Record dto) {
        UpdatePackageDateDTO updateDto = (UpdatePackageDateDTO) dto;

        PackageDate existing = packageDateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PackageDate não encontrado"));

        existing.setStartDate(updateDto.startDate());
        existing.setEndDate(updateDto.endDate());

        packageDateRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        PackageDate existing = packageDateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PackageDate não encontrado"));
        packageDateRepository.delete(existing);
    }
}
