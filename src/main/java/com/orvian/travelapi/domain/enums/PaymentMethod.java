package com.orvian.travelapi.domain.enums;

public enum PaymentMethod {
    CREDITO,
    DEBITO,
    BOLETO,
    PIX;

    public static PaymentMethod fromString(String value) {
        return PaymentMethod.valueOf(value.trim().toUpperCase());
    }
}
