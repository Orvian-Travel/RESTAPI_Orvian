package com.orvian.travelapi.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "TB_PACKAGES")
@Getter
@Setter
public class TravelPackage {

    @Id
    @GeneratedValue
    @Column(name = "ID", columnDefinition = "uniqueidentifier", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "TITLE", nullable = false, length = 150)
    private String title;

    @Column(name = "DESCRIPTION_PACKAGE", nullable = false)
    private String description;

    @Column(name = "DESTINATION", nullable = false, length = 50)
    private String destination;

    @Min(1)
    @Column(name = "DURATION", nullable = false)
    private int duration;

    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "MAX_PEOPLE", nullable = false)
    private int maxPeople;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
