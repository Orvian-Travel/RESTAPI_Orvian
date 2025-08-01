package com.orvian.travelapi.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_PASSWORD_RESET_TOKENS")
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "TOKEN", nullable = false, unique = true)
    private String token;

    @Column(name = "USER_EMAIL", nullable = false, length = 150)
    private String userEmail;

    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

    @Column(name = "USED", nullable = false)
    private boolean used = false;
}
