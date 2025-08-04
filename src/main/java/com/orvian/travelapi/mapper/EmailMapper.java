package com.orvian.travelapi.mapper;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.orvian.travelapi.controller.dto.email.EmailConfirmationDTO;
import com.orvian.travelapi.domain.enums.PaymentMethod;
import com.orvian.travelapi.domain.model.Payment;
import com.orvian.travelapi.domain.model.Reservation;

/**
 * Mapper responsável por transformar entidades em DTOs para envio de email
 * Segue o padrão MapStruct utilizado no projeto
 */
@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface EmailMapper {

    /**
     * Converte dados do Payment (com relacionamentos) para EmailConfirmationDTO
     * Acessa todos os relacionamentos necessários: Payment -> Reservation ->
     * User/PackageDate -> TravelPackage
     */
    @Mapping(target = "reservationId", source = "reservation.id")
    @Mapping(target = "customerName", source = "reservation.user.name")
    @Mapping(target = "customerEmail", source = "reservation.user.email")
    @Mapping(target = "packageTitle", source = "reservation.packageDate.travelPackage.title")
    @Mapping(target = "packageDestination", source = "reservation.packageDate.travelPackage.destination")
    @Mapping(target = "tripStartDate", source = "reservation.packageDate.startDate")
    @Mapping(target = "tripEndDate", source = "reservation.packageDate.endDate")
    @Mapping(target = "totalAmountPaid", source = "valuePaid")
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodToString")
    @Mapping(target = "paymentApprovedAt", source = "paymentApprovedAt")
    @Mapping(target = "totalTravelers", source = "reservation", qualifiedByName = "calculateTotalTravelers")
    EmailConfirmationDTO toEmailConfirmationDTO(Payment payment);

    @Named("paymentMethodToString")
    default String paymentMethodToString(PaymentMethod paymentMethod) {
        return paymentMethod != null ? paymentMethod.name() : "NÃO_INFORMADO";
    }

    @Named("calculateTotalTravelers")
    default Integer calculateTotalTravelers(Reservation reservation) {

        int totalTravelers = reservation.getTravelers() != null
                ? reservation.getTravelers().size() : 0;

        return totalTravelers;
    }

    default LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now();
    }
}
