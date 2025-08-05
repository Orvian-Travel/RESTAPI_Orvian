package com.orvian.travelapi.controller.dto.admin;

import java.math.BigDecimal;
import java.sql.Date;

public record ReservationToSheetDTO(
        String reservationId,
        String packageId,
        BigDecimal price,
        String packageTitle,
        Date reservationDate,
        String situation,
        String userEmail, // ✅ MOVIDO PARA 7º LUGAR
        Integer qtdViajantes, // ✅ MOVIDO PARA 8º LUGAR  
        Date cancelDate // ✅ MOVIDO PARA 9º LUGAR
        ) {

}
