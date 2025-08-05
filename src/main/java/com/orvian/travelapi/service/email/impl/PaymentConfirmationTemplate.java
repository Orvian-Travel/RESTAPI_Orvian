package com.orvian.travelapi.service.email.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.orvian.travelapi.config.EmailConfigProperties;
import com.orvian.travelapi.controller.dto.email.EmailConfirmationDTO;
import com.orvian.travelapi.service.email.EmailTemplateStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConfirmationTemplate implements EmailTemplateStrategy {

    private final EmailConfigProperties emailProperties;

    @Override
    public String buildSubject(Object data) {
        EmailConfirmationDTO emailData = (EmailConfirmationDTO) data;
        return String.format("✈️ Orvian Travel - Pagamento Aprovado - Reserva #%s",
                emailData.reservationId().toString().substring(0, 8));
    }

    @Override
    public String buildHtmlContent(Object data) {
        EmailConfirmationDTO emailData = (EmailConfirmationDTO) data;
        String formattedDateTime = formatDateTimeForBrazilian(emailData.paymentApprovedAt());

        return String.format("""
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Confirmação de Pagamento - Orvian Travel</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                    <div style="background: linear-gradient(135deg, #004657 0%%, #005a6b 100%%); color: white; padding: 30px 20px; text-align: center;">
                        <h1 style="margin: 0; font-size: 28px; font-weight: bold;">✈️ ORVIAN TRAVEL</h1>
                        <div style="background-color: #f7a700; color: #004657; display: inline-block; padding: 8px 16px; border-radius: 20px; margin-top: 15px; font-weight: bold;">
                            PAGAMENTO APROVADO
                        </div>
                    </div>
                    <div style="padding: 30px 20px;">
                        <h2 style="color: #004657; margin-top: 0;">Olá %s! 👋</h2>
                        <div style="background-color: #e8f5e8; border-left: 4px solid #f7a700; padding: 15px; margin: 20px 0; border-radius: 4px;">
                            <p style="margin: 0; color: #004657; font-weight: bold; font-size: 16px;">
                                🎉 EXCELENTES NOTÍCIAS! Seu pagamento foi aprovado!
                            </p>
                        </div>
                        <!-- Resto do template... -->
                        <div style="text-align: center; padding: 20px; background-color: #f7a700; border-radius: 8px; margin: 20px 0;">
                            <p style="margin: 0; color: #004657; font-weight: bold;">📞 SUPORTE: %s</p>
                        </div>
                    </div>
                    <div style="background-color: #004657; color: white; text-align: center; padding: 20px;">
                        <p style="margin: 0; font-size: 14px;">© 2024 Orvian Travel - Todos os direitos reservados</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                emailData.customerName(),
                emailProperties.getSupport()
        );
    }

    @Override
    public String getTemplateType() {
        return "PAYMENT_CONFIRMATION";
    }

    private String formatDateTimeForBrazilian(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Data não informada";
        }

        try {
            ZoneId brasiliaZone = ZoneId.of("America/Sao_Paulo");
            ZoneId utcZone = ZoneId.of("UTC");

            ZonedDateTime utcDateTime = dateTime.atZone(utcZone);
            ZonedDateTime brasiliaDateTime = utcDateTime.withZoneSameInstant(brasiliaZone);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy 'às' HH:mm",
                    Locale.forLanguageTag("pt-BR")
            );

            return brasiliaDateTime.format(formatter);

        } catch (Exception e) {
            log.warn("Failed to format datetime for Brazilian timezone: {}", e.getMessage());
            DateTimeFormatter fallbackFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return dateTime.format(fallbackFormatter) + " (UTC)";
        }
    }
}
