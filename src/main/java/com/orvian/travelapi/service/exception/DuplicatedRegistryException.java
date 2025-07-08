package com.orvian.travelapi.service.exception;

public class DuplicatedRegistryException extends RuntimeException {
    public DuplicatedRegistryException(String message) {
        super(message);
    }
}
