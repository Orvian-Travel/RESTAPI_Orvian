package com.orvian.travelapi.controller.dto;

import com.orvian.travelapi.annotation.Adult;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserDTO(
        @Size(max = 150, message = "Name must be at most 150 characters long")
        String name,
        @Size(max = 150, message = "Email must be at most 150 characters long")
        @Email(message = "Email must be valid")
        String email,
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters long")
        String password,
        @Size(max = 15, message = "Phone not in valid format")
        String phone,
        @Size(min = 8, max = 14, message = "Document not in valid format")
        String document,
        @Adult
        LocalDate birthDate
) {
}
