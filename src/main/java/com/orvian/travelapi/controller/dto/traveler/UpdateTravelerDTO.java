package com.orvian.travelapi.controller.dto.traveler;

import com.orvian.travelapi.annotation.Adult;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateTravelerDTO(
        @Size(max = 150, message = "Name must be at most 150 characters long")
        String name,

        @Size(max = 150, message = "Email must be at most 150 characters long")
        String  email,

        @Size(min = 14, max = 14, message = "CPF must be 14 characters long")
        String cpf,

        @Adult
        LocalDate birthDate
) {
}
