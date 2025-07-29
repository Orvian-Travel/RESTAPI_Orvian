package com.orvian.travelapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Configurações de email para o sistema Orvian Travel Mapeia automaticamente
 * propriedades do arquivo application.yml
 */
@Data
@Component
@ConfigurationProperties(prefix = "orvian.email")
public class EmailConfigProperties {

    /**
     * Email de origem para envio de notificações
     */
    private String from = "noreply@orviantravelapi.com";

    /**
     * Email de suporte técnico
     */
    private String support = "suporte@orviantravelapi.com";
}
