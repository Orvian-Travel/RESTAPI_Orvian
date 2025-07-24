package com.orvian.travelapi.domain.enums.converter;

import com.orvian.travelapi.domain.enums.PaymentMethod;

import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodConverter extends GenericEnumConverter<PaymentMethod> {

    public PaymentMethodConverter() {
        super(PaymentMethod.class);
    }
}
