package com.orvian.travelapi.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.orvian.travelapi.domain.model.Payment;
import com.orvian.travelapi.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Override
    public Object findAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Payment create(Record dto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object findById(UUID id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(UUID id, Record dto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    // TODO implement do serviço Payment
}
