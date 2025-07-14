package com.orvian.travelapi.controller.dto.user;

import com.orvian.travelapi.annotation.Adult;
import com.orvian.travelapi.annotation.Document;
import com.orvian.travelapi.annotation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(
        name = "CreateUserDTO",
        description = "Data Transfer Object for creating a new user",
        title = "Create User DTO"
)
public record CreateUserDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 150, message = "Name must be at most 150 characters long")
        @Schema(name = "name", description = "User's full name", example = "John Doe")
        String name,
        @NotBlank(message = "Email is required")
        @Size(max = 150, message = "Email must be at most 150 characters long")
        @Email(message = "Email must be valid")
        @Schema(name = "email", description = "User's email address", example = "example@example.com")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters long")
        @Schema(name = "password", description = "Password for the user account", example = "securePassword123")
        @Password
        String password,
        @NotBlank(message = "Phone is required")
        @Size(max = 15, message = "Phone not in valid format")
        @Schema(name = "phone", description = "User's phone number", example = "(12) 34567-8910")
        String phone,
        @NotBlank(message = "Document is required")
        @Size(min = 8, max = 14, message = "Document not in valid format")
        @Schema(name = "document", description = "User's identification document number", example = "123.456.789-10 or AZ123456")
        @Document
        String document,
        @NotNull(message = "Birth date is required")
        @Adult
        @Schema(name = "birthDate", description = "User's birth date in ISO format (YYYY-MM-DD)", example = "1990-01-01")
        LocalDate birthDate,
        @NotBlank(message = "Role is required")
        @Size(max = 20, message = "Role must be at most 20 characters long")
        @Schema(name = "role", description = "User's role in the system", example = "USER")
        String role
) {
}
