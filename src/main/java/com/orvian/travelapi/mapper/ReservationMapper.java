package com.orvian.travelapi.mapper;

import com.orvian.travelapi.controller.dto.packagedate.SearchPackageDateDTO;
import com.orvian.travelapi.controller.dto.reservation.CreateReservationDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationSearchResultDTO;
import com.orvian.travelapi.domain.model.PackageDate;
import com.orvian.travelapi.domain.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper{
    @Mapping(target = "travelers", source = "travelers")
    @Mapping(target = "payment", source = "payment")
    ReservationSearchResultDTO toDTO(Reservation reservation);
    SearchPackageDateDTO toDTO(PackageDate packageDate);
    Reservation toEntity(CreateReservationDTO createReservationDTO);
}

