package com.orvian.travelapi.domain.model;

import com.orvian.travelapi.domain.enums.ReservationSituation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "TB_RESERVATIONS")
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue
    @Column(name = "ID", columnDefinition = "uniqueidentifier", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "RESERVATION_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date reservationDate;
    @Column(name = "SITUATION", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationSituation situation;
    @Column(name = "CANCEL_DATE")
    @Temporal(TemporalType.DATE)
    private Date cancelledDate;
    @ManyToOne
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;
    // @ManyToOne
    // @JoinColumn(name = "ID_PACKAGES_DATES", nullable = false)
    // private PackageDate packageDate;
    @Column(name = "CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
