package com.orvian.travelapi.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.orvian.travelapi.controller.dto.payment.CreatePaymentDTO;
import com.orvian.travelapi.domain.model.Payment;
import com.orvian.travelapi.domain.repository.PaymentRepository;
import com.orvian.travelapi.mapper.PaymentMapper;
import com.orvian.travelapi.service.PaymentService;
import com.orvian.travelapi.service.exception.BusinessException;
import com.orvian.travelapi.service.exception.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public Object findAll() {
        log.info("Retrieving all payments");
        return paymentMapper.toPaymentSearchResultDTOList(paymentRepository.findAll());

    }

    @Override
    public Payment create(Record dto) {
        Payment payment = paymentMapper.toEntity((CreatePaymentDTO) dto);
        log.info("Creating payment with ID: {}", payment.getId());

        if (paymentRepository.existsById(payment.getId())) {
            String errorMsg = "Payment with ID " + payment.getId() + " already exists";
            log.error(errorMsg);
            throw new BusinessException(errorMsg);
        }

        try {
            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment created with ID: {}", savedPayment.getId());
            return savedPayment;
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for payment update: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid argument provided for payment update: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            handleErrorSQL(e);
            return null; // This line will not be reached, but is needed to satisfy the compiler
        } catch (Exception e) {
            log.error("Unexpected error creating payment: {}", e.getMessage());
            throw new RuntimeException("Unexpected error creating payment: " + e.getMessage());
        }
    }

    @Override
    public Object findById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found with ID: " + id));
        log.info("Payment found with id: {}", id);
        return paymentMapper.toDTO(payment);
    }

    @Override
    public void update(UUID id, Record dto) {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty()) {
            log.error("Payment not found with ID: {}", id);
            throw new NotFoundException("Payment not found with ID: " + id);
        }

        Payment payment = paymentOptional.get();
        log.info("Updating payment with ID: {}", id);
        paymentMapper.updateEntityFromDTO((CreatePaymentDTO) dto, payment);

        try {
            paymentRepository.save(payment);
            log.info("Payment updated with ID: {}", payment.getId());

        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for payment update: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid argument provided for payment update: " + e.getMessage());

        } catch (DataIntegrityViolationException e) {
            handleErrorSQL(e);

        } catch (Exception e) {
            log.error("Unexpected error updating payment: {}", e.getMessage());
            throw new RuntimeException("Unexpected error updating payment: " + e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void handleErrorSQL(DataIntegrityViolationException e) {
        Throwable rootCause = e.getRootCause();
        String message = (rootCause != null && rootCause.getMessage() != null)
                ? rootCause.getMessage()
                : e.getMessage();
        log.error("Database integrity error: {}", message);
        throw new BusinessException("Falha ao criar pagamento: " + message);
    }

}
