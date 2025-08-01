package com.orvian.travelapi.service;

import java.util.List;
import java.util.UUID;

import com.orvian.travelapi.controller.dto.payment.PaymentSearchResultDTO;
import com.orvian.travelapi.domain.model.Payment;

public interface PaymentService extends CrudService<UUID, Payment> {

    @Override
    List<PaymentSearchResultDTO> findAll();

    @Override
    PaymentSearchResultDTO findById(UUID id);
}
