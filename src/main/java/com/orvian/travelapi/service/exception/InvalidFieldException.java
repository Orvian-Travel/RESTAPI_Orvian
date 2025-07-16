package com.orvian.travelapi.service.exception;

import lombok.Getter;

/*
    Exceção lançada quando um campo de algum DTO é inválido.
 */
public class InvalidFieldException extends RuntimeException {

    @Getter
    private final String field;

    /*
          Constrói uma nova exceção de campo inválido com o campo e a mensagem.
     */
    public InvalidFieldException(String field, String message) {
        super(message);
        this.field = field;
    }
}

