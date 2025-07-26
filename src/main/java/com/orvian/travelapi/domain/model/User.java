package com.orvian.travelapi.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_USERS")
@Getter
@Setter
@Schema(description = "User entity representing a user in the system.", title = "User", name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(name = "id", description = "User's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Column(name = "NAME", nullable = false, length = 150)
    @Schema(name = "name", description = "User's full name", example = "John Doe")
    private String name;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 150)
    @Schema(name = "email", description = "User's email address", example = "example@example.com")
    private String email;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    @Schema(name = "password", description = "User's password", example = "hashed_password")
    private String password;

    @Column(name = "PHONE", nullable = false, length = 15)
    @Schema(name = "phone", description = "User's phone number", example = "(12) 34567-8910")
    private String phone;

    @Column(name = "DOCUMENT", nullable = true, length = 14)
    @Schema(name = "document", description = "User's identification document number", example = "123.456.789-10 or AZ123456")
    private String document;

    @Column(name = "BIRTHDATE", nullable = false)
    @Schema(name = "birthDate", description = "User's birth date in ISO format (YYYY-MM-DD)", example = "1990-01-01")
    private LocalDate birthDate;

    @Column(name = "ROLE", nullable = false, length = 20)
    @Schema(name = "role", description = "User's role in the system", example = "USER")
    private String  role = "USER";

    @Column(name = "CREATED_AT", nullable = false)
    @Schema(name = "createdAt", description = "Timestamp when the user was created", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    @Schema(name = "updatedAt", description = "Timestamp when the user was last updated", example = "2023-10-01T12:00:00")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
