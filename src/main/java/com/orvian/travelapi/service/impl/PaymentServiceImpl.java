package com.orvian.travelapi.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.orvian.travelapi.controller.dto.payment.CreatePaymentDTO;
import com.orvian.travelapi.controller.dto.payment.PaymentSearchResultDTO;
import com.orvian.travelapi.controller.dto.payment.UpdatePaymentDTO;
import com.orvian.travelapi.domain.model.Payment;
import com.orvian.travelapi.domain.repository.PaymentRepository;
import com.orvian.travelapi.mapper.PaymentMapper;
import com.orvian.travelapi.service.PaymentService;
import com.orvian.travelapi.service.exception.NotFoundException;
import static com.orvian.travelapi.service.exception.PersistenceExceptionUtil.handlePersistenceError;

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
    public List<PaymentSearchResultDTO> findAll() {
        try {
            log.info("Retrieving all payments");
            List<Payment> payments = paymentRepository.findAll();
            return paymentMapper.toPaymentSearchResultDTOList(payments);
        } catch (Exception e) {
            log.error("Error fetching payments: {}", e.getMessage(), e);
            throw new NotFoundException("Erro ao buscar pagamentos: " + e.getMessage());
        }

    }

    @Override
    public Payment create(Record dto) {

        try {
            Payment payment = paymentMapper.toEntity((CreatePaymentDTO) dto);
            log.info("Creating payment with VALUE_PAID: {}", payment.getValuePaid());

            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment created with ID: {}", savedPayment.getId());
            return savedPayment;
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for payment update: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid argument provided for payment update: " + e.getMessage());
        } catch (RuntimeException e) {
            handlePersistenceError(e, log);
            return null; // This line will not be reached, but is needed to satisfy the compiler
        }
    }

    @Override
    public PaymentSearchResultDTO findById(UUID id) {
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

        try {
            Payment payment = paymentOptional.get();
            log.info("Updating payment with ID: {}", id);
            paymentMapper.updateEntityFromDTO((UpdatePaymentDTO) dto, payment);

            paymentRepository.save(payment);
            log.info("Payment updated with ID: {}", payment.getId());

        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for payment update: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid argument provided for payment update: " + e.getMessage());

        } catch (RuntimeException e) {
            handlePersistenceError(e, log);
        }
    }

    @Override
    public void delete(UUID id) {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);

        if (paymentOptional.isEmpty()) {
            log.error("Payment not found with ID: {}", id);
            throw new NotFoundException("Payment not found with ID: " + id);
        }

        Payment payment = paymentOptional.get();
        log.info("Deleting payment with ID: {}", id);
        paymentRepository.delete(payment);
        log.info("Payment deleted with ID: {}", id);
    }

}
