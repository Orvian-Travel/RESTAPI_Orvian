package com.orvian.travelapi.domain.enums.converter;

import com.orvian.travelapi.domain.enums.PaymentStatus;

import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentStatusConverter extends GenericEnumConverter<PaymentStatus> {

    public PaymentStatusConverter() {
        super(PaymentStatus.class);
    }
}
