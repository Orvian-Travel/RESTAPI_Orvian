package com.orvian.travelapi.domain.enums.converter;

import com.orvian.travelapi.domain.enums.ReservationSituation;

import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReservationSituationConverter extends GenericEnumConverter<ReservationSituation> {

    public ReservationSituationConverter() {
        super(ReservationSituation.class);
    }
}
