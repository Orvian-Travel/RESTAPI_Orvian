package com.orvian.travelapi.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "TB_RATINGS")
@Getter
@Setter
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "RATE", nullable = false)
    private int rate;

    @Column(name = "COMMENT", length = 250)
    private String comment;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDate createdAt = LocalDate.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDate updatedAt = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "ID_RESERVATION", nullable = false)
    private Reservation reservation;
}
