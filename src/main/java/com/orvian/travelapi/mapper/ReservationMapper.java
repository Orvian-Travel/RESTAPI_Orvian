package com.orvian.travelapi.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.orvian.travelapi.controller.dto.payment.CreatePaymentDTO;
import com.orvian.travelapi.controller.dto.payment.PaymentSearchResultDTO;
import com.orvian.travelapi.controller.dto.reservation.CreateReservationDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationSearchResultDTO;
import com.orvian.travelapi.controller.dto.reservation.UpdateReservationDTO;
import com.orvian.travelapi.controller.dto.traveler.CreateTravelerDTO;
import com.orvian.travelapi.domain.model.Payment;
import com.orvian.travelapi.domain.model.Reservation;
import com.orvian.travelapi.domain.model.Traveler;

@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface ReservationMapper {

    // @Mapping(target = "travelers", source = "travelers")
    // @Mapping(target = "payment", source = "payment")
    Reservation toEntity(CreateReservationDTO createReservationDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UpdateReservationDTO dto, @MappingTarget Reservation reservation);

    ReservationSearchResultDTO toDTO(Reservation reservation);

    List<PaymentSearchResultDTO> toPaymentSearchResultDTOList(List<Reservation> reservationList);

    Traveler toEntity(CreateTravelerDTO dto);

    Payment toEntity(CreatePaymentDTO dto);

    @Mapping(target = "id", source = "reservation.id")
    @Mapping(target = "createdAt", source = "reservation.createdAt")
    @Mapping(target = "updateAt", source = "reservation.updateAt")
    @Mapping(target = "payment", source = "payment")
    ReservationSearchResultDTO toDTO(Reservation reservation, Payment payment);
}
