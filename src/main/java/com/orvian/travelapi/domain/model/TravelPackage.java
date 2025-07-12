package com.orvian.travelapi.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
    private int duration;
    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(name = "MAX_PEOPLE", nullable = false)
    private int maxPeople;
    @Column(name = "CREATED_AT", nullable = false)
    private Date createdAt;
    @Column(name = "UPDATED_AT", nullable = false)
    private Date updatedAt;
}
