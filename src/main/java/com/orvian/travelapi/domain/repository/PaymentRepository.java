package com.orvian.travelapi.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orvian.travelapi.domain.enums.PaymentMethod;
import com.orvian.travelapi.domain.enums.PaymentStatus;
import com.orvian.travelapi.domain.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
}
