package com.orvian.travelapi.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.orvian.travelapi.controller.dto.packagedate.UpdatePackageDateDTO;
import com.orvian.travelapi.controller.dto.travelpackage.CreateTravelPackageDTO;
import com.orvian.travelapi.controller.dto.travelpackage.PackageSearchResultDTO;
import com.orvian.travelapi.controller.dto.travelpackage.UpdateTravelPackageDTO;
import com.orvian.travelapi.domain.enums.ReservationSituation;
import com.orvian.travelapi.domain.model.Media;
import com.orvian.travelapi.domain.model.PackageDate;
import com.orvian.travelapi.domain.model.TravelPackage;
import com.orvian.travelapi.domain.repository.MediaRepository;
import com.orvian.travelapi.domain.repository.PackageDateRepository;
import com.orvian.travelapi.domain.repository.ReservationRepository;
import com.orvian.travelapi.domain.repository.TravelPackageRepository;
import com.orvian.travelapi.mapper.TravelPackageMapper;
import com.orvian.travelapi.service.TravelPackageService;
import com.orvian.travelapi.service.exception.BusinessException;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import com.orvian.travelapi.service.exception.NotFoundException;
import static com.orvian.travelapi.service.exception.PersistenceExceptionUtil.handlePersistenceError;
import static com.orvian.travelapi.specs.TravelPackageSpecs.hasStartDateFrom;
import static com.orvian.travelapi.specs.TravelPackageSpecs.maxPeopleGreaterThanOrEqual;
import static com.orvian.travelapi.specs.TravelPackageSpecs.titleLike;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PackageServiceImpl implements TravelPackageService {

    private final TravelPackageRepository travelPackageRepository;
    private final TravelPackageMapper travelPackageMapper;
    private final PackageDateRepository packageDateRepository;
    private final MediaRepository mediaRepository;
    private final ReservationRepository reservationRepository;

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

                        Optional<Media> firstMedia = mediaRepository.findFirstByTravelPackage_IdOrderByCreatedAtAsc(pkg.getId());
                        List<Media> mediaList = firstMedia.map(List::of).orElse(List.of());

                        return travelPackageMapper.toDTOWithDatesAndFirstMedia(pkg, dates, mediaList);
                    });
        } catch (Exception e) {
            log.error("Erro ao buscar pacotes: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar pacotes: " + e.getMessage());
        }
    }

    @Override
    public Page<PackageSearchResultDTO> findAllBySearch(Integer pageNumber, Integer pageSize, String title,
            LocalDate startDate, Integer maxPeople) {
        try {
            log.info("Retrieving travel packages with filters - title: {}, startDate: {}, maxPeople: {}",
                    title, startDate, maxPeople);

            Specification<TravelPackage> spec = (root, query, cb) -> cb.conjunction();

            if (title != null && !title.isBlank()) {
                spec = spec.and(titleLike(title));
            }
            if (startDate != null) {
                spec = spec.and(hasStartDateFrom(startDate));
            }
            if (maxPeople != null) {
                spec = spec.and(maxPeopleGreaterThanOrEqual(maxPeople));
            }

            Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());

            return travelPackageRepository
                    .findAll(spec, pageRequest)
                    .map(pkg -> {
                        List<PackageDate> dates = packageDateRepository.findByTravelPackage_Id(pkg.getId());

                        Optional<Media> firstMedia = mediaRepository.findFirstByTravelPackage_IdOrderByCreatedAtAsc(pkg.getId());
                        List<Media> mediaList = firstMedia.map(List::of).orElse(List.of());

                        return travelPackageMapper.toDTOWithDatesAndFirstMedia(pkg, dates, mediaList);
                    });

        } catch (Exception e) {
            log.error("Erro ao buscar pacotes: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar pacotes: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public TravelPackage create(Record dto) {
        try {
            CreateTravelPackageDTO dtoTravelPackage = (CreateTravelPackageDTO) dto;
            log.info("Creating new travel package with title: {}", dtoTravelPackage.title());

            TravelPackage travelPackage = travelPackageMapper.toTravelPackage(dtoTravelPackage);
            validateCreationAndUpdate(travelPackage);

            TravelPackage savedPackage = travelPackageRepository.save(travelPackage);

            List<PackageDate> packageDates = travelPackageMapper.createPackageDatesForPackage(
                    dtoTravelPackage.packageDates(),
                    savedPackage
            );

            if (!packageDates.isEmpty()) {
                packageDateRepository.saveAll(packageDates);
                log.info("Created {} package dates for travel package: {}", packageDates.size(), savedPackage.getId());
            }

            if (dtoTravelPackage.medias() != null && !dtoTravelPackage.medias().isEmpty()) {
                List<Media> medias = travelPackageMapper.createMediasForPackage(
                        dtoTravelPackage.medias(),
                        savedPackage
                );

                mediaRepository.saveAll(medias);
                log.info("Created {} medias for travel package: {}", medias.size(), savedPackage.getId());
            }

            return savedPackage;

        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for package creation: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid argument provided for package creation: " + e.getMessage());
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

        List<PackageDate> dates = packageDateRepository.findByTravelPackage_Id(travelPackage.getId());

        List<Media> medias = mediaRepository.findByTravelPackage_IdOrderByCreatedAtAsc(travelPackage.getId());

        return travelPackageMapper.toDTOWithDatesAndMedias(travelPackage, dates, medias);
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
            UpdateTravelPackageDTO updateDto = (UpdateTravelPackageDTO) dto;

            log.info("Updating Package with ID: {}", travelPackage.getId());

            // 1. Atualizar dados do TravelPackage
            travelPackageMapper.updateEntityFromDto(updateDto, travelPackage);
            validateCreationAndUpdate(travelPackage);
            TravelPackage savedPackage = travelPackageRepository.save(travelPackage);

            // 2. ✅ Atualizar PackageDates se fornecido
            if (updateDto.packageDates() != null) {
                updatePackageDatesIncremental(savedPackage, updateDto.packageDates());
            }

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
        try {
            log.info("Attempting to delete travel package with ID: {}", id);

            TravelPackage travelPackage = travelPackageRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Travel package not found for deletion with ID: {}", id);
                        return new NotFoundException("Travel package with ID " + id + " not found.");
                    });

            // Verificar se há reservas associadas (regra de negócio), Comentada para testes
            // validatePackageDeletion(travelPackage);
            travelPackageRepository.deleteById(id);
            log.info("Travel package with ID: {} deleted successfully", id);

        } catch (NotFoundException e) {
            // Re-throw business exceptions
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete travel package with ID {}: {}", id, e.getMessage(), e);
            handlePersistenceError(e, log);
        }
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

        return packageOptional.isPresent() && !travelPackage.getId().equals(packageOptional.get().getId());
    }

    private void updatePackageDatesIncremental(TravelPackage travelPackage, List<UpdatePackageDateDTO> packageDatesDTO) {
        try {
            log.info("Incrementally updating package dates for travel package: {}", travelPackage.getId());

            List<PackageDate> existingDates = packageDateRepository.findByTravelPackage_Id(travelPackage.getId());

            if (packageDatesDTO == null || packageDatesDTO.isEmpty()) {
                log.info("No package dates to update - keeping existing ones");
                return;
            }

            // Mapear datas existentes por ID (se tiverem ID no DTO)
            Map<UUID, PackageDate> existingDatesMap = existingDates.stream()
                    .collect(Collectors.toMap(PackageDate::getId, date -> date));

            List<PackageDate> updatedDates = new ArrayList<>();
            Set<UUID> processedIds = new HashSet<>();

            for (UpdatePackageDateDTO dto : packageDatesDTO) {
                if (dto.id() != null && existingDatesMap.containsKey(dto.id())) {
                    // Atualizar existente
                    PackageDate existing = existingDatesMap.get(dto.id());
                    travelPackageMapper.updatePackageDateFromDto(dto, existing);
                    updatedDates.add(existing);
                    processedIds.add(dto.id());
                } else {
                    // Criar novo
                    PackageDate newDate = travelPackageMapper.toPackageDate(dto, travelPackage);
                    updatedDates.add(newDate);
                }
            }

            // Remover datas que não foram processadas
            List<PackageDate> datesToRemove = existingDates.stream()
                    .filter(date -> !processedIds.contains(date.getId()))
                    .toList();

            if (!datesToRemove.isEmpty()) {
                packageDateRepository.deleteAll(datesToRemove);
                log.info("Removed {} package dates", datesToRemove.size());
            }

            // Salvar atualizações
            packageDateRepository.saveAll(updatedDates);
            log.info("Updated/created {} package dates", updatedDates.size());

        } catch (Exception e) {
            log.error("Error incrementally updating package dates for travel package {}: {}",
                    travelPackage.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update package dates: " + e.getMessage());
        }
    }

    private void validatePackageDeletion(TravelPackage travelPackage) {

        if (reservationRepository.existsByPackageDate_TravelPackage_IdAndSituationNot(
                travelPackage.getId(), ReservationSituation.CANCELADA)) {
            throw new BusinessException("Cannot delete package with active reservations");
        }
    }
}
