package com.orvian.travelapi.service.impl;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.orvian.travelapi.config.EmailConfigProperties;
import com.orvian.travelapi.controller.dto.email.EmailConfirmationDTO;
import com.orvian.travelapi.service.EmailNotificationService;
import com.orvian.travelapi.service.email.EmailTemplateFactory;
import com.orvian.travelapi.service.email.EmailTemplateStrategy;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do serviço de notificação por email Utiliza JavaMailSender do
 * Spring para enviar emails via SMTP
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final JavaMailSender mailSender;
    private final EmailConfigProperties emailProperties;
    private final EmailTemplateFactory templateFactory;

    @Override
    public void sendPaymentConfirmationEmail(EmailConfirmationDTO data) {
        try {
            log.info("Starting payment confirmation email sending to: {}", data.customerEmail());

            EmailTemplateStrategy template = templateFactory.getTemplate("PAYMENT_CONFIRMATION");

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(emailProperties.getFrom());
            helper.setTo(data.customerEmail());
            helper.setSubject(template.buildSubject(data));
            helper.setText(template.buildHtmlContent(data), true);

            mailSender.send(mimeMessage);

            log.info("Payment confirmation email sent successfully for reservation: {}", data.reservationId());

        } catch (MailException | MessagingException e) {
            log.error("Failed to send payment confirmation email for reservation {}: {}",
                    data.reservationId(), e.getMessage(), e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String userEmail, String resetLink) {
        try {
            EmailTemplateStrategy template = templateFactory.getTemplate("PASSWORD_RESET");

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(emailProperties.getFrom());
            helper.setTo(userEmail);
            helper.setSubject(template.buildSubject(resetLink));
            helper.setText(template.buildHtmlContent(resetLink), true);

            mailSender.send(mimeMessage);

        } catch (MailException | MessagingException e) {
            log.error("Falha ao enviar email de redefinição de senha para {}: {}", userEmail, e.getMessage(), e);
        }
    }
}
