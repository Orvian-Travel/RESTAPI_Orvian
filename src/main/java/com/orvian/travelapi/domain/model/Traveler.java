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
@Table(name = "TB_TRAVELERS")
@Getter
@Setter
@Schema(
        description = "Traveler entity representing a traveler in the system.",
        title = "Traveler",
        name = "Traveler"
)
public class Traveler {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(name = "id", description = "Traveler's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Column(name = "NAME", length = 150, nullable = false)
    @Schema(name = "name", description = "Traveler's full name", example = "John Doe")
    private String name;

    // email esta unico no BD, mas aqui não pode ter o caso de ter o mesmo viajante em outras reservas (outras datas)?
    @Column(name = "EMAIL", length = 150, nullable = false)
    @Schema(name = "email", description = "Traveler's email address", example = "example@example.com")
    private String email;

    @Column(name = "CPF", length = 14, nullable = false)
    @Schema(name = "cpf", description = "Traveler's cpf number", example = "123.456.789-10")
    private String cpf;

    @Column(name = "BIRTHDATE", nullable = false)
    @Schema(name = "birthDate", description = "Traveler's birth date in ISO format (YYYY-MM-DD)", example = "1990-01-01")
    private LocalDate birthDate;

    // relação com as reservas (comentado caso não esteja totalmente implementado na outra ponta)
    // @JoinColumn(name = "ID_RESERVATION", nullable = false)
    // @ManyToOne(fetch = FetchType.LAZY)
    // private Reservation reservation;
    @Column(name = "CREATED_AT", nullable = false)
    @Schema(name = "createdAt", description = "Timestamp when the traveler was created", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    @Schema(name = "updatedAt", description = "Timestamp when the traveler was created", example = "2023-10-01T12:00:00")
    private LocalDateTime updatedAt = LocalDateTime.now();

}
