package com.orvian.travelapi.domain.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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

    @Lob
    @Column(name = "CONTENT64", nullable = false, columnDefinition = "VARBINARY(MAX)")
    private byte[] content64;

    @Column(name = "TYPE", nullable = false, length = 10)
    private String type;

    @ManyToOne
    @JoinColumn(name = "PACKAGE_ID", nullable = false)
    private TravelPackage travelPackage;

    @Column(name = "CREATED_AT", nullable = false)
    @Schema(name = "createdAt", description = "Timestamp when the reservation was created", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    @Schema(name = "updatedAt", description = "Timestamp when the reservation was last updated", example = "2023-10-01T12:00:00")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);
        this.createdAt = utcNow;
        this.updatedAt = utcNow;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }
}
