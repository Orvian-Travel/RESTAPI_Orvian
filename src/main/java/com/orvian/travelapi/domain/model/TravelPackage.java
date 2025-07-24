package com.orvian.travelapi.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_PACKAGES")
@Getter
@Setter
public class TravelPackage {

    @Id
    @GeneratedValue
    @Column(name = "ID", columnDefinition = "uniqueidentifier", updatable = false, nullable = false)
    @Schema(name = "id", description = "Unique identifier for the travel package", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Column(name = "TITLE", nullable = false, length = 150)
    @Schema(name = "title", description = "Title of the travel package", example = "Paris Getaway")
    private String title;

    @Column(name = "DESCRIPTION_PACKAGE", nullable = false)
    @Schema(name = "description", description = "Description of the travel package", example = "A week-long trip to explore the beautiful city of Paris, including flights, accommodation, and guided tours.")
    private String description;

    @Column(name = "DESTINATION", nullable = false, length = 50)
    @Schema(name = "destination", description = "Destination of the travel package", example = "Paris, France")
    private String destination;

    @Min(1)
    @Column(name = "DURATION", nullable = false)
    @Schema(name = "duration", description = "Duration of the travel package in days", example = "7")
    private int duration;

    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    @Schema(name = "price", description = "Price of the travel package", example = "1999.99")
    private BigDecimal price;

    @Column(name = "MAX_PEOPLE", nullable = false)
    @Schema(name = "maxPeople", description = "Maximum number of people allowed for this travel package", example = "10")
    private int maxPeople;

    @Column(name = "CREATED_AT", nullable = false)
    @Schema(name = "createdAt", description = "Timestamp when the travel package was created", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    @Schema(name = "updatedAt", description = "Timestamp when the travel package was last updated", example = "2023-10-01T12:00:00")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
