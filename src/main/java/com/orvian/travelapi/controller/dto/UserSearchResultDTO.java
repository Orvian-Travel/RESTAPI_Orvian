package com.orvian.travelapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(
        name = "UserSearchResultDTO",
        description = "Data Transfer Object for user search results",
        title = "User Search Result DTO"
)
public record UserSearchResultDTO(
        @Schema(name = "id", description = "User's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(name = "name", description = "User's full name", example = "John Doe")
        String name,
        @Schema(name = "email", description = "User's email address", example = "example@example.com")
        String email,
        @Schema(name = "phone", description = "User's phone number", example = "(12) 34567-8901")
        String phone,
        @Schema(name = "document", description = "User's document number", example = "123.456.789-10 or AZ123456")
        String document,
        @Schema(name = "birthDate", description = "User's date of birth in ISO format (YYYY-MM-DD)", example = "1990-01-01")
        LocalDate birthDate
) {
}
