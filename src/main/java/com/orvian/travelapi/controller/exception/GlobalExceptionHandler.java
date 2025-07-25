package com.orvian.travelapi.controller.exception;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.orvian.travelapi.controller.dto.error.FieldErrorDTO;
import com.orvian.travelapi.controller.dto.error.ResponseErrorDTO;
import com.orvian.travelapi.service.exception.BusinessException;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import com.orvian.travelapi.service.exception.InvalidFieldException;
import com.orvian.travelapi.service.exception.NotFoundException;

import jakarta.servlet.http.HttpServletRequest;

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
    public ResponseErrorDTO handleDuplicatedRegistryException(DuplicatedRegistryException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        return ResponseErrorDTO.conflict(e.getMessage(), path);
    }

    /*
        Trata exceções de regra de negócio, retornando um status 400 (BAD REQUEST).
        Exemplo: ao tentar realizar uma operação não permitida pelo sistema.
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleBusinessException(BusinessException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        return ResponseErrorDTO.defaultResponse(e.getMessage(), path);
    }

    /*
        Trata exceções de campo inválido, retornando um status 400 (BAD REQUEST).
        Exemplo: ao tentar criar um usuário com um email inválido.
     */
    @ExceptionHandler(InvalidFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleInvalidFieldException(InvalidFieldException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        return ResponseErrorDTO.of(
                HttpStatus.BAD_REQUEST,
                "Validation error",
                List.of(new FieldErrorDTO(e.getField(), e.getMessage())),
                path
        );
    }

    /*
        Trata exceções de validação de argumentos de método, retornando um status 400 (BAD REQUEST).
        Exemplo: ao tentar criar um usuário com campos obrigatórios não preenchidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseErrorDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<FieldErrorDTO> fieldErrorsList = fieldErrors.stream()
                .map(error -> new FieldErrorDTO(error.getField(), error.getDefaultMessage()))
                .toList();
        String path = request.getRequestURI();
        return ResponseErrorDTO.unprocessableEntity("Validation error", fieldErrorsList, path);
    }

    /*
        Trata exceções de recurso não encontrado, retornando um status 404 (NOT FOUND).
        Exemplo: ao tentar buscar um usuário que não existe.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseErrorDTO handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        return ResponseErrorDTO.of(HttpStatus.NOT_FOUND, e.getMessage(), List.of(), path);
    }

    /*
        Trata exceções genéricas de tempo de execução, retornando um status 500 (INTERNAL SERVER ERROR).
        Exemplo: ao ocorrer um erro inesperado no servidor.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseErrorDTO handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        return ResponseErrorDTO.of(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", List.of(), path);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleJsonParseError(HttpMessageNotReadableException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        return ResponseErrorDTO.of(HttpStatus.BAD_REQUEST, "Erro ao ler JSON: " + e.getMessage(), List.of(), path);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseErrorDTO handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        String path = request.getRequestURI();
        String message = "Database integrity error";
        Throwable root = e.getRootCause();
        if (root != null && root.getMessage() != null) {
            message = root.getMessage();
        }
        return ResponseErrorDTO.of(HttpStatus.CONFLICT, message, List.of(), path);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseErrorDTO handleGenericException(Exception e, HttpServletRequest request) {
        String path = request.getRequestURI();
        // Log detalhado para análise interna
        return ResponseErrorDTO.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                List.of(),
                path
        );
    }

}
