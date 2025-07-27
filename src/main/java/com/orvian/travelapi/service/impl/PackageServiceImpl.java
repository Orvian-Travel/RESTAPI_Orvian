package com.orvian.travelapi.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.orvian.travelapi.controller.dto.travelpackage.CreateTravelPackageDTO;
import com.orvian.travelapi.controller.dto.travelpackage.PackageSearchResultDTO;
import com.orvian.travelapi.controller.dto.travelpackage.UpdateTravelPackageDTO;
import com.orvian.travelapi.domain.model.PackageDate;
import com.orvian.travelapi.domain.model.TravelPackage;
import com.orvian.travelapi.domain.repository.PackageDateRepository;
import com.orvian.travelapi.domain.repository.TravelPackageRepository;
import com.orvian.travelapi.mapper.TravelPackageMapper;
import com.orvian.travelapi.service.TravelPackageService;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import com.orvian.travelapi.service.exception.NotFoundException;
import static com.orvian.travelapi.service.exception.PersistenceExceptionUtil.handlePersistenceError;
import static com.orvian.travelapi.specs.TravelPackageSpecs.titleLike;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PackageServiceImpl implements TravelPackageService {

    private final TravelPackageRepository travelPackageRepository;
    private final TravelPackageMapper travelPackageMapper;
    private final PackageDateRepository packageDateRepository;

    @Override
    public Page<PackageSearchResultDTO> findAll(Integer pageNumber, Integer pageSize, String title) {
        try {
            log.info("Retrieving all travel packages with title: {}", title);
            Specification<TravelPackage> spec = (title != null && !title.isBlank()) ? titleLike(title) : null;
            Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());

            return travelPackageRepository
                    .findAll(spec, pageRequest)
                    .map(pkg -> {

                        List<PackageDate> dates = packageDateRepository.findByTravelPackage_Id(pkg.getId());

                        return travelPackageMapper.toDTO(pkg, dates);
                    });
        } catch (Exception e) {
            log.error("Erro ao buscar reservas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar reservas: " + e.getMessage());
        }
    }

    @Override
    public TravelPackage create(Record dto) {
        try {
            TravelPackage travelPackage = travelPackageMapper.toTravelPackage((CreateTravelPackageDTO) dto);
            validateCreationAndUpdate(travelPackage);
            return travelPackageRepository.save(travelPackage);
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for reservation creation: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid argument provided for reservation creation: " + e.getMessage());
        } catch (RuntimeException e) {
            handlePersistenceError(e, log);
            return null;
        }
    }

    @Override
    public PackageSearchResultDTO findById(UUID id) {
        TravelPackage travelPackage = travelPackageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Travel package with ID " + id + " not found."));
        log.info("Travel package found with ID: {}", id);

        // Busca as datas desse pacote
        List<PackageDate> dates = packageDateRepository.findByTravelPackage_Id(travelPackage.getId());

        // Monte o DTO, passando as datas
        return travelPackageMapper.toDTO(travelPackage, dates);
    }

    @Override
    public void update(UUID id, Record dto) {
        Optional<TravelPackage> packageOptional = travelPackageRepository.findById(id);
        if (packageOptional.isEmpty()) {
            log.error("Travel package not found with ID: {}", id);
            throw new NotFoundException("Travel package not found with ID: " + id);
        }

        try {
            TravelPackage travelPackage = packageOptional.get();
            log.info("Updating Package with ID: {}", travelPackage.getId());
            validateCreationAndUpdate(travelPackage);

            travelPackageMapper.updateEntityFromDto((UpdateTravelPackageDTO) dto, travelPackage);

            travelPackageRepository.save(travelPackage);
            log.info("Package with ID: {} updated successfully", travelPackage.getId());
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for payment update: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid argument provided for payment update: " + e.getMessage());

        } catch (RuntimeException e) {
            handlePersistenceError(e, log);
        }
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
