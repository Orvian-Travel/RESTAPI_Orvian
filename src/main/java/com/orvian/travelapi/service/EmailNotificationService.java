package com.orvian.travelapi.service;

import com.orvian.travelapi.controller.dto.email.EmailConfirmationDTO;

public interface EmailNotificationService {

    void sendPaymentConfirmationEmail(EmailConfirmationDTO confirmationData);
    void sendPasswordResetEmail(String to, String resetLink);
}
