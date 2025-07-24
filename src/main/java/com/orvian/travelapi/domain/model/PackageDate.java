package com.orvian.travelapi.domain.model;

import com.orvian.travelapi.domain.model.TravelPackage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "TB_PACKAGES_DATES")
@Getter
@Setter
public class PackageDate {

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "Identificador único do pacote de data", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID id;

    @Column(name = "START_DATE", nullable = false)
    @Schema(description = "Data de início do pacote", example = "2024-07-01")
    private LocalDate startDate;

    @Column(name = "END_DATE", nullable = false)
    @Schema(description = "Data de término do pacote", example = "2024-07-10")
    private LocalDate endDate;

    @Column(name = "QTD_AVAILABLE", nullable = false)
    @Schema(description = "Quantidade disponível para o pacote", example = "20")
    private int qtd_available;

    @JoinColumn(name = "ID_PACKAGES", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "Referência ao pacote de viagem relacionado")
    private TravelPackage travelPackage;

}