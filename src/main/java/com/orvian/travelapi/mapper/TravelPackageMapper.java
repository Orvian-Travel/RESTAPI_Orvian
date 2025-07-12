package com.orvian.travelapi.mapper;

import com.orvian.travelapi.controller.dto.CreateTravelPackageDTO;
import com.orvian.travelapi.controller.dto.PackageSearchResultDTO;
import com.orvian.travelapi.domain.model.TravelPackage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TravelPackageMapper {
    TravelPackage toTravelPackage(CreateTravelPackageDTO dto);
    PackageSearchResultDTO toPackageSearchResultDTO(TravelPackage entity);
    List<PackageSearchResultDTO> toPackageSearchResultDTOList(List<TravelPackage> entities);
}