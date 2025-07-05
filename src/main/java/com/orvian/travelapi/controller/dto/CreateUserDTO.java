package com.orvian.travelapi.controller.dto;

import com.orvian.travelapi.annotation.Adult;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateUserDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must be at most 150 characters long")
        String name,
        @NotBlank(message = "Email is required")
        @Size(max = 150, message = "Email must be at most 150 characters long")
        @Email(message = "Email must be valid")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters long")
        String password,
        @NotBlank(message = "Phone is required")
        @Size(max = 15, message = "Phone not in valid format")
        String phone,
        @NotBlank(message = "Document is required")
        @Size(min = 8, max = 14, message = "Document not in valid format")
        String document,
        @NotNull(message = "Birth date is required")
        @Adult
        LocalDate birthDate,
        @NotBlank(message = "Role is required")
        String role
) {
}
