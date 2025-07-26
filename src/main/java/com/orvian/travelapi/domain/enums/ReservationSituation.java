package com.orvian.travelapi.domain.enums;

public enum ReservationSituation {
    PENDENTE,
    CONFIRMADA,
    CANCELADA;

    public static ReservationSituation fromString(String value) {
        return ReservationSituation.valueOf(value.trim().toUpperCase());
    }
}
