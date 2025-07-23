package com.orvian.travelapi.domain.model;

import com.orvian.travelapi.domain.enums.ReservationSituation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_RESERVATIONS")
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue
    @Column(name = "ID", columnDefinition = "uniqueidentifier", updatable = false, nullable = false)
    @Schema(name = "id", description = "Unique identifier for the reservation", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Column(name = "RESERVATION_DATE", nullable = false)
    @Schema(name = "reservationDate", description = "Date of the reservation", example = "2023-10-01")
    private LocalDate reservationDate;

    @Column(name = "SITUATION", nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(name = "situation", description = "Current situation of the reservation", example = "CONFIRMED")
    private ReservationSituation situation;

    @Column(name = "CANCEL_DATE")
    @Schema(name = "cancelDate", description = "Date when the reservation was cancelled", example = "2023-10-02")
    private LocalDate cancelledDate;

    @ManyToOne
    @JoinColumn(name = "ID_USER", nullable = false)
    @Schema(name = "idUser", description = "User who made the reservation")
    private User user;

    // @ManyToOne
    // @JoinColumn(name = "ID_PACKAGES_DATES", nullable = false)
    // @Schema(name = "idPackageDate", description = "Travel package date associated with the reservation", example = "123e4567-e89b-12d3-a456-426614174001")
    // private PackageDate packageDate;
    @Column(name = "CREATED_AT", nullable = false)
    @Schema(name = "createdAt", description = "Timestamp when the reservation was created", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATE_AT", nullable = false)
    @Schema(name = "updateAt", description = "Timestamp when the reservation was last updated", example = "2023-10-01T12:00:00")
    private LocalDateTime updateAt = LocalDateTime.now();
}
