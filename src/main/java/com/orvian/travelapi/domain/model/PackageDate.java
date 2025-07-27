package com.orvian.travelapi.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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

    @JoinColumn(name = "ID_PACKAGE", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "Referência ao pacote de viagem relacionado")
    private TravelPackage travelPackage;

}
