package com.orvian.travelapi.service.email;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailTemplateFactory {

    private final List<EmailTemplateStrategy> templateStrategies;

    private Map<String, EmailTemplateStrategy> getTemplateMap() {
        return templateStrategies.stream()
                .collect(Collectors.toMap(
                        EmailTemplateStrategy::getTemplateType,
                        Function.identity()
                ));
    }

    public EmailTemplateStrategy getTemplate(String templateType) {
        EmailTemplateStrategy template = getTemplateMap().get(templateType);

        if (template == null) {
            throw new IllegalArgumentException("Template not found for type: " + templateType);
        }

        return template;
    }
}
