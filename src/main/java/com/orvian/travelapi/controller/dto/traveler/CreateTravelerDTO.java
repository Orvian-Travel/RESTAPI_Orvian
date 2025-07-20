package com.orvian.travelapi.controller.dto.traveler;

import com.orvian.travelapi.annotation.Adult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTravelerDTO (

        @NotBlank(message = "name is required")
        @Size(max = 150, message = "Name must be at most 150 characters long")
        String name,

        @NotBlank(message = "email is required")
        @Size(max = 150, message = "Email must be at most 150 characters long")
        String  email,

        @NotBlank(message = "cpf is required")
        @Size(min = 14, max = 14, message = "CPF must be 14 characters long")
        String cpf,

        @NotBlank(message = "birthdate is required")
        @Adult
        LocalDate birthDate
){
}
