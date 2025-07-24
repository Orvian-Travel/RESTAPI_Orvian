package com.orvian.travelapi.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.orvian.travelapi.controller.dto.payment.CreatePaymentDTO;
import com.orvian.travelapi.controller.dto.payment.PaymentSearchResultDTO;
import com.orvian.travelapi.controller.dto.payment.UpdatePaymentDTO;
import com.orvian.travelapi.domain.model.Payment;

@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface PaymentMapper {

    Payment toEntity(CreatePaymentDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UpdatePaymentDTO dto, @MappingTarget Payment payment);

    PaymentSearchResultDTO toDTO(Payment payment);

    List<PaymentSearchResultDTO> toPaymentSearchResultDTOList(List<Payment> paymentList);
}
