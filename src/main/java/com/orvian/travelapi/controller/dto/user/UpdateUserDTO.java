package com.orvian.travelapi.controller.dto.user;

import com.orvian.travelapi.annotation.Adult;
import com.orvian.travelapi.annotation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(
        name = "UpdateUserDTO",
        description = "Data Transfer Object for updating an existing user",
        title = "Update User DTO"
)
public record UpdateUserDTO(
        @Size(max = 150, message = "Name must be at most 150 characters long")
        @Schema(name = "name", description = "User's full name", example = "John Doe")
        String name,
        @Size(max = 150, message = "Email must be at most 150 characters long")
        @Email(message = "Email must be valid")
        @Schema(name = "email", description = "User's email address", example = "example@example.com")
        String email,
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters long")
        @Schema(name = "password", description = "Password for the user account", example = "securePassword123")
        @Password
        String password,
        @Size(max = 15, message = "Phone not in valid format")
        @Schema(name = "phone", description = "User's phone number", example = "(12) 34567-8910")
        String phone,
        @Size(min = 8, max = 14, message = "Document not in valid format")
        @Schema(name = "document", description = "User's identification document number", example = "123.456.789-10 or AZ123456")
        String document,
        @Adult
        @Schema(name = "birthDate", description = "User's birth date in ISO format (YYYY-MM-DD)", example = "1990-01-01")
        LocalDate birthDate
) {
}
