package com.orvian.travelapi.mapper;

import com.orvian.travelapi.controller.dto.traveler.CreateTravelerDTO;
import com.orvian.travelapi.controller.dto.traveler.TravelerSearchResultDTO;
import com.orvian.travelapi.controller.dto.traveler.UpdateTravelerDTO;
import com.orvian.travelapi.domain.model.Traveler;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TravelerMapper {

    Traveler toEntity(CreateTravelerDTO dto);

    void updateEntityFromDto(UpdateTravelerDTO dto, @MappingTarget Traveler traveler);

    TravelerSearchResultDTO toDto(Traveler traveler);

    List<TravelerSearchResultDTO> toTravelerSearchResultDTOList(List<Traveler> travelerList);
}
