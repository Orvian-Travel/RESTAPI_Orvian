package com.orvian.travelapi.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.orvian.travelapi.controller.dto.packagedate.SearchPackageDateDTO;
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

    // Método para conversão com datas (nome diferente)
    @Mapping(target = "packageDates", source = "packageDates")
    PackageSearchResultDTO toDTOWithDates(TravelPackage entity, List<PackageDate> packageDates);

    @Mapping(target = "travelPackageId", source = "travelPackage.id")
    SearchPackageDateDTO toDTO(PackageDate entity);
}
