package com.orvian.travelapi.controller.exception;

import com.orvian.travelapi.controller.dto.error.FieldErrorDTO;
import com.orvian.travelapi.controller.dto.error.ResponseErrorDTO;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import com.orvian.travelapi.service.exception.InvalidFieldException;
import com.orvian.travelapi.service.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/*
    GlobalExceptionHandler padrão do Spring Boot para tratar exceções de forma centralizada.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
        Trata exceções de duplicação de registro, retornando um status 409 (CONFLICT).
        Exemplo: ao tentar criar um usuário com um email já existente.
     */
    @ExceptionHandler(DuplicatedRegistryException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseErrorDTO handleDuplicatedRegistryException(DuplicatedRegistryException e) {
        return ResponseErrorDTO.conflict(e.getMessage());
    }

    /*
        Trata exceções de campo inválido, retornando um status 400 (BAD REQUEST).
        Exemplo: ao tentar criar um usuário com um email inválido.
     */
    @ExceptionHandler(InvalidFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleInvalidFieldException(InvalidFieldException e) {
        return new ResponseErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error",
                List.of(new FieldErrorDTO(e.getField(), e.getMessage()))
        );
    }

    /*
        Trata exceções de validação de argumentos de método, retornando um status 400 (BAD REQUEST).
        Exemplo: ao tentar criar um usuário com campos obrigatórios não preenchidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<FieldErrorDTO> fieldErrorsList = fieldErrors.stream()
                .map(error -> new FieldErrorDTO(error.getField(), error.getDefaultMessage()))
                .toList();
        return new ResponseErrorDTO(HttpStatus.BAD_REQUEST.value(), "Validation error", fieldErrorsList);
    }

    /*
        Trata exceções de recurso não encontrado, retornando um status 404 (NOT FOUND).
        Exemplo: ao tentar buscar um usuário que não existe.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseErrorDTO handleNotFoundException(NotFoundException e) {
        return new ResponseErrorDTO(HttpStatus.NOT_FOUND.value(), e.getMessage(), List.of());
    }

    /*
        Trata exceções genéricas de tempo de execução, retornando um status 500 (INTERNAL SERVER ERROR).
        Exemplo: ao ocorrer um erro inesperado no servidor.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseErrorDTO handleRuntimeException(RuntimeException e) {
        return new ResponseErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred", List.of());
    }
}
