package com.orvian.travelapi.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "TB_MEDIAS")
@Getter
@Setter
@Schema(
        name = "Media",
        description = "Represents media content associated with a travel package, such as images or videos.",
        title = "Media Entity"
)
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "CONTENT64", nullable = false)
    private String content64;

    @Column(name = "TYPE", nullable = false, length = 10)
    private String type;

    @ManyToOne
    @JoinColumn(name = "TRAVEL_PACKAGE_ID", nullable = false)
    private TravelPackage travelPackage;
}
