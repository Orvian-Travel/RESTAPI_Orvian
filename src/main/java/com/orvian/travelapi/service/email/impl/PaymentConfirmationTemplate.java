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
            
            <!-- Container principal -->
            <div style="max-width: 600px; margin: 0 auto; background-color: white; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                
                <!-- Header colorido com gradiente -->
                <div style="background: linear-gradient(135deg, #004657 0%%, #005a6b 100%%); color: white; padding: 30px 20px; text-align: center;">
                    <h1 style="margin: 0; font-size: 28px; font-weight: bold;">
                        ✈️ ORVIAN TRAVEL
                    </h1>
                    <div style="background-color: #f7a700; color: #004657; display: inline-block; padding: 8px 16px; border-radius: 20px; margin-top: 15px; font-weight: bold;">
                        PAGAMENTO APROVADO
                    </div>
                </div>

                <!-- Conteúdo principal -->
                <div style="padding: 30px 20px;">
                    
                    <!-- Saudação -->
                    <h2 style="color: #004657; margin-top: 0;">Olá %s! 👋</h2>
                    
                    <!-- Mensagem de sucesso -->
                    <div style="background-color: #e8f5e8; border-left: 4px solid #f7a700; padding: 15px; margin: 20px 0; border-radius: 4px;">
                        <p style="margin: 0; color: #004657; font-weight: bold; font-size: 16px;">
                            🎉 EXCELENTES NOTÍCIAS! Seu pagamento foi aprovado!
                        </p>
                    </div>

                    <!-- Detalhes da reserva -->
                    <div style="background-color: #f9f9f9; padding: 20px; border-radius: 8px; margin: 20px 0;">
                        <h3 style="color: #004657; margin-top: 0; border-bottom: 2px solid #f7a700; padding-bottom: 10px;">
                            📋 DETALHES DA SUA RESERVA
                        </h3>
                        
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 8px 0; color: #666; font-weight: bold;">Reserva ID:</td>
                                <td style="padding: 8px 0; color: #004657;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #666; font-weight: bold;">Pacote:</td>
                                <td style="padding: 8px 0; color: #004657; font-weight: bold;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #666; font-weight: bold;">Destino:</td>
                                <td style="padding: 8px 0; color: #004657;">🌍 %s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #666; font-weight: bold;">Data de ida:</td>
                                <td style="padding: 8px 0; color: #004657;">📅 %s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #666; font-weight: bold;">Data de volta:</td>
                                <td style="padding: 8px 0; color: #004657;">📅 %s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #666; font-weight: bold;">Viajantes:</td>
                                <td style="padding: 8px 0; color: #004657;">👥 %s pessoa(s)</td>
                            </tr>
                        </table>
                    </div>

                    <!-- Informações do pagamento -->
                    <div style="background-color: #fff8e7; padding: 20px; border-radius: 8px; margin: 20px 0; border: 1px solid #f7a700;">
                        <h3 style="color: #004657; margin-top: 0; border-bottom: 2px solid #f7a700; padding-bottom: 10px;">
                            💳 INFORMAÇÕES DO PAGAMENTO
                        </h3>
                        
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 8px 0; color: #666; font-weight: bold;">Valor total pago:</td>
                                <td style="padding: 8px 0; color: #004657; font-weight: bold; font-size: 18px;">💰 R$ %s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #666; font-weight: bold;">Método:</td>
                                <td style="padding: 8px 0; color: #004657;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #666; font-weight: bold;">Aprovado em:</td>
                                <td style="padding: 8px 0; color: #004657;">⏰ %s</td>
                            </tr>
                        </table>
                    </div>

                    <!-- Próximos passos -->
                    <div style="background-color: #e7f3ff; padding: 20px; border-radius: 8px; margin: 20px 0;">
                        <h3 style="color: #004657; margin-top: 0;">🎫 PRÓXIMOS PASSOS</h3>
                        <ul style="color: #004657; padding-left: 20px;">
                            <li style="margin: 8px 0;">Aguarde o envio dos vouchers por email</li>
                            <li style="margin: 8px 0;">Mantenha este email como comprovante</li>
                            <li style="margin: 8px 0;">Entre em contato conosco para dúvidas</li>
                        </ul>
                    </div>

                    <!-- Informações de contato -->
                    <div style="text-align: center; padding: 20px; background-color: #f7a700; border-radius: 8px; margin: 20px 0;">
                        <p style="margin: 0; color: #004657; font-weight: bold;">
                            📞 SUPORTE: %s
                        </p>
                    </div>

                    <!-- Agradecimento -->
                    <div style="text-align: center; padding: 20px 0;">
                        <p style="color: #004657; font-size: 16px; margin: 10px 0;">
                            <strong>Obrigado por escolher a Orvian Travel!</strong>
                        </p>
                        <p style="color: #f7a700; font-size: 18px; margin: 10px 0;">
                            <strong>Tenha uma excelente viagem! ✈️🌎</strong>
                        </p>
                    </div>
                </div>

                <!-- Footer -->
                <div style="background-color: #004657; color: white; text-align: center; padding: 20px;">
                    <p style="margin: 0; font-size: 14px;">
                        © 2024 Orvian Travel - Todos os direitos reservados
                    </p>
                    <p style="margin: 5px 0 0 0; font-size: 12px; opacity: 0.8;">
                        Este é um email automático, não responda esta mensagem.
                    </p>
                </div>

            </div>
        </body>
        </html>
        """,
                emailData.customerName(), // %s - Nome do cliente
                emailData.reservationId(), // %s - ID da reserva
                emailData.packageTitle(), // %s - Título do pacote
                emailData.packageDestination(), // %s - Destino
                emailData.tripStartDate(), // %s - Data de ida
                emailData.tripEndDate(), // %s - Data de volta
                emailData.totalTravelers(), // %s - Número de viajantes
                emailData.totalAmountPaid(), // %s - Valor total pago
                emailData.paymentMethod(), // %s - Método de pagamento
                formattedDateTime, // %s - Data/hora da aprovação
                emailProperties.getSupport() // %s - Suporte
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
