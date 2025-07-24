package com.orvian.travelapi.domain.enums;

public enum PaymentStatus {
    APROVADO,
    CANCELADO,
    PENDENTE;

    public static PaymentStatus fromString(String value) {
        return PaymentStatus.valueOf(value.trim().toUpperCase());
    }
}
