package com.orvian.travelapi.service.exception;

import org.slf4j.Logger;

public class PersistenceExceptionUtil {

    private PersistenceExceptionUtil() {
    } // Utilitário, não instanciável

    public static void handlePersistenceError(Throwable e, Logger log) {
        Throwable rootCause = e;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        String message = (rootCause.getMessage() != null) ? rootCause.getMessage() : e.getMessage();
        log.error("Persistence error: {}", message);
        throw new BusinessException("Failed to process entity: " + message);
    }
}
