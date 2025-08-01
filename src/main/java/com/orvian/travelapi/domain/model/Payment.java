package com.orvian.travelapi.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import com.orvian.travelapi.domain.enums.PaymentMethod;
import com.orvian.travelapi.domain.enums.PaymentStatus;
import com.orvian.travelapi.domain.enums.converter.PaymentMethodConverter;
import com.orvian.travelapi.domain.enums.converter.PaymentStatusConverter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_PAYMENTS")
@Getter
@Setter
@Schema(description = "Payments entity representing payment transactions in the system.", title = "Payments", name = "Payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", columnDefinition = "uniqueidentifier", updatable = false, nullable = false)
    @Schema(name = "id", description = "Payment's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Column(name = "VALUE_PAID", nullable = false, precision = 10, scale = 2)
    @Schema(name = "valuePaid", description = "Amount paid in the payment transaction", example = "100.00")
    private BigDecimal valuePaid;

    // @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD", nullable = false, length = 15)
    @Convert(converter = PaymentMethodConverter.class)
    @Schema(name = "paymentMethod", description = "Payment method used for the transaction", example = "CREDIT_CARD")
    private PaymentMethod paymentMethod;

    @Column(name = "PAYMENT_STATUS", nullable = false, length = 15)
    @Convert(converter = PaymentStatusConverter.class)
    @Schema(name = "status", description = "Current status of the payment", example = "COMPLETED")
    private PaymentStatus status;

    @Column(name = "PAYMENT_APPROVED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(name = "paymentApprovedAt", description = "Timestamp when the payment was approved", example = "2023-10-01T12:00:00")
    private Date paymentApprovedAt;

    @Column(name = "TAX")
    @Schema(name = "tax", description = "Tax amount applied to the payment", example = "10.00%")
    private Double tax;

    @Column(name = "INSTALLMENT")
    @Schema(name = "installment", description = "Installment number for the payment", example = "1")
    private Integer installment;

    @Column(name = "INSTALLMENT_AMOUNT", precision = 10, scale = 2)
    @Schema(name = "installmentAmount", description = "Amount for each installment", example = "50.00")
    private BigDecimal installmentAmount;

    @OneToOne
    @JoinColumn(name = "ID_RESERVATION")
    private Reservation reservation;

    @Column(name = "CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(name = "createdAt", description = "Timestamp when the PAYMENT was created", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(name = "updatedAt", description = "Timestamp when the PAYMENT was last updated", example = "2023-10-01T12:00:00")
    private LocalDateTime updatedAt = LocalDateTime.now();

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
