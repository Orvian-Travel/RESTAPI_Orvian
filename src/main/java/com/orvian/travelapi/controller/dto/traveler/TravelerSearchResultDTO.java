package com.orvian.travelapi.controller.dto.traveler;

import java.time.LocalDate;
import java.util.UUID;

public record TravelerSearchResultDTO(
        UUID id,
        String name,
        String email,
        String cpf,
        LocalDate birthDate

) {
}
