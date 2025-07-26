package com.orvian.travelapi.controller.dto.packagedate;

import java.time.LocalDate;
import java.util.UUID;

public record SearchPackageDateDTO(
        UUID id,
        LocalDate startDate,
        LocalDate endDate,
        int qtd_available,
        UUID travelPackageId
        ) {

}
