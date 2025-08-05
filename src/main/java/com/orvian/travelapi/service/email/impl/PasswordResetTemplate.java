package com.orvian.travelapi.service.email.impl;

import org.springframework.stereotype.Component;

import com.orvian.travelapi.service.email.EmailTemplateStrategy;

@Component
public class PasswordResetTemplate implements EmailTemplateStrategy {

    @Override
    public String buildSubject(Object data) {
        return "🔒 Redefinição de senha - Orvian Travel";
    }

    @Override
    public String buildHtmlContent(Object data) {
        String resetLink = (String) data;

        return String.format("""
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <title>Redefinição de Senha</title>
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <div style="max-width: 600px; margin: 0 auto; background: white; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                    <div style="background: linear-gradient(135deg, #004657 0%%, #005a6b 100%%); color: white; padding: 30px 20px; text-align: center;">
                        <h1 style="margin: 0;">🔒 ORVIAN TRAVEL</h1>
                        <div style="background-color: #f7a700; color: #004657; display: inline-block; padding: 8px 16px; border-radius: 20px; margin-top: 15px; font-weight: bold;">
                            REDEFINIÇÃO DE SENHA
                        </div>
                    </div>
                    <div style="padding: 30px 20px;">
                        <h2 style="color: #004657;">Olá!</h2>
                        <p>Recebemos uma solicitação para redefinir sua senha.</p>
                        <div style="background-color: #e7f3ff; padding: 20px; border-radius: 8px; margin: 20px 0;">
                            <p style="color: #004657; font-weight: bold;">Para criar uma nova senha, clique no botão abaixo:</p>
                            <a href="%s" style="display: inline-block; background: #f7a700; color: #004657; padding: 12px 24px; border-radius: 6px; text-decoration: none; font-weight: bold; margin-top: 10px;">
                                Redefinir senha
                            </a>
                        </div>
                        <p>Se você não solicitou essa alteração, ignore este e-mail.</p>
                        <p style="color: #888; font-size: 12px;">Este link expira em 1 hora.</p>
                    </div>
                    <div style="background-color: #004657; color: white; text-align: center; padding: 20px;">
                        <p style="margin: 0; font-size: 14px;">© 2024 Orvian Travel - Todos os direitos reservados</p>
                        <p style="margin: 5px 0 0 0; font-size: 12px; opacity: 0.8;">
                            Este é um email automático, não responda esta mensagem.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """, resetLink);
    }

    @Override
    public String getTemplateType() {
        return "PASSWORD_RESET";
    }
}
