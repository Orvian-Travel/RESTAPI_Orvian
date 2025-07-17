package com.orvian.travelapi.mapper;

import com.orvian.travelapi.controller.dto.travelpackage.CreateTravelPackageDTO;
import com.orvian.travelapi.controller.dto.travelpackage.PackageSearchResultDTO;
import com.orvian.travelapi.controller.dto.travelpackage.UpdateTravelPackageDTO;
import com.orvian.travelapi.domain.model.TravelPackage;
import jakarta.validation.Valid;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TravelPackageMapper {

    TravelPackage toTravelPackage(CreateTravelPackageDTO dto);

    PackageSearchResultDTO toPackageSearchResultDTO(TravelPackage entity);

    List<PackageSearchResultDTO> toPackageSearchResultDTOList(List<TravelPackage> entities);

    TravelPackage toUpdateTravelPackage(UpdateTravelPackageDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateTravelPackageDTO dto, @MappingTarget TravelPackage travelPackage);
}