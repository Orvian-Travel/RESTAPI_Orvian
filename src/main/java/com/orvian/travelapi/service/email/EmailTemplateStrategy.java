package com.orvian.travelapi.service.email;

public interface EmailTemplateStrategy {

    String buildSubject(Object data);

    String buildHtmlContent(Object data);

    String getTemplateType();
}
