package com.orvian.travelapi.service.exception;

/**
 * Exceção personalizada para casos de acesso negado no sistema Orvian Lançada
 * quando um usuário tenta acessar recursos sem a devida autorização
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
