package com.orvian.travelapi.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.orvian.travelapi.controller.dto.media.CreateMediaDTO;
import com.orvian.travelapi.controller.dto.media.SearchMediaDTO;
import com.orvian.travelapi.domain.model.Media;
import org.mapstruct.*;

import com.orvian.travelapi.controller.dto.packagedate.CreatePackageDateDTO;
import com.orvian.travelapi.controller.dto.packagedate.SearchPackageDateDTO;
import com.orvian.travelapi.controller.dto.packagedate.UpdatePackageDateDTO;
import com.orvian.travelapi.controller.dto.travelpackage.CreateTravelPackageDTO;
import com.orvian.travelapi.controller.dto.travelpackage.PackageSearchResultDTO;
import com.orvian.travelapi.controller.dto.travelpackage.UpdateTravelPackageDTO;
import com.orvian.travelapi.domain.model.PackageDate;
import com.orvian.travelapi.domain.model.TravelPackage;

@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface TravelPackageMapper {

    TravelPackage toTravelPackage(CreateTravelPackageDTO dto);

    // Método para conversão simples (sem datas)
    PackageSearchResultDTO toDTO(TravelPackage entity);

    List<PackageSearchResultDTO> toPackageSearchResultDTOList(List<TravelPackage> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateTravelPackageDTO dto, @MappingTarget TravelPackage travelPackage);

    @Mapping(target = "travelPackageId", source = "travelPackage.id")
    SearchPackageDateDTO toDTO(PackageDate entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "travelPackage", source = "travelPackage")
    PackageDate toPackageDate(CreatePackageDateDTO dto, TravelPackage travelPackage);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "travelPackage", source = "travelPackage")
    PackageDate toPackageDate(UpdatePackageDateDTO dto, TravelPackage travelPackage);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "travelPackage", ignore = true)
    void updatePackageDateFromDto(UpdatePackageDateDTO dto, @MappingTarget PackageDate packageDate);

    default List<PackageDate> createPackageDatesForPackage(
            List<CreatePackageDateDTO> packageDatesDTO,
            TravelPackage travelPackage) {

        if (packageDatesDTO == null || packageDatesDTO.isEmpty()) {
            return List.of();
        }

        return packageDatesDTO.stream()
                .map(dto -> toPackageDate(dto, travelPackage))
                .toList();
    }

    default List<PackageDate> updatePackageDatesForPackage(
            List<UpdatePackageDateDTO> packageDatesDTO,
            List<PackageDate> existingPackageDates,
            TravelPackage travelPackage) {

        if (packageDatesDTO == null || packageDatesDTO.isEmpty()) {
            return existingPackageDates; // Manter os existentes se não fornecido
        }

        // Se fornecido packageDates, substitui todos
        return packageDatesDTO.stream()
                .map(dto -> toPackageDate(dto, travelPackage))
                .toList();
    }

    default List<Media> createMediasForPackage(List<CreateMediaDTO> mediaDTOS, TravelPackage travelPackage) {
        if (mediaDTOS == null || mediaDTOS.isEmpty()) {
            return new ArrayList<>();
        }

        return mediaDTOS.stream()
                .map(dto -> {
                    Media media = new Media();
                    media.setContent64(Base64.getDecoder().decode(dto.content64()));
                    media.setType(dto.type());
                    media.setTravelPackage(travelPackage);
                    return media;
                })
                .collect(Collectors.toList());
    }

    default SearchMediaDTO toSearchMediaDTO(Media media) {
        return new SearchMediaDTO(
                media.getId(),
                media.getType(),
                java.util.Base64.getEncoder().encodeToString(media.getContent64())
        );
    }

    default List<SearchMediaDTO> toSearchMediaDTOList(List<Media> medias) {
        if (medias == null) return List.of();
        return medias.stream()
                .map(media -> new SearchMediaDTO(
                        media.getId(),
                        media.getType(),
                        java.util.Base64.getEncoder().encodeToString(media.getContent64())
                ))
                .toList();
    }

    default PackageSearchResultDTO toDTOWithDates(TravelPackage entity, List<PackageDate> packageDates) {
        List<SearchPackageDateDTO> dateDTOs = packageDates == null ? List.of() :
                packageDates.stream().map(this::toDTO).toList();

        List<SearchMediaDTO> mediaDTOs = toSearchMediaDTOList(entity.getMedia());

        return new PackageSearchResultDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDestination(),
                entity.getDuration(),
                entity.getPrice(),
                entity.getMaxPeople(),
                dateDTOs,
                mediaDTOs,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}