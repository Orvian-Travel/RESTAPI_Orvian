package com.orvian.travelapi.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.orvian.travelapi.domain.model.PasswordResetToken;
import com.orvian.travelapi.domain.repository.PasswordResetTokenRepository;
import com.orvian.travelapi.service.impl.UserServiceImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final EmailNotificationService emailNotificationService;
    private final PasswordResetTokenRepository tokenRepository;
    private final UserServiceImpl userService;

    public void requestPasswordReset(String email) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setUserEmail(email);
        tokenRepository.save(resetToken);
        String resetLink = "https://orvian-travel.azurewebsites.net/reset-password/" + resetToken.getToken();

        emailNotificationService.sendPasswordResetEmail(email, resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Password reset token has already been used");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token has expired");
        }

        userService.resetUserPassword(resetToken.getUserEmail(), newPassword);
    }
}
