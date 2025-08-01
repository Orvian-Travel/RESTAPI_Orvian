package com.orvian.travelapi.annotation.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação personalizada para validar se o usuário pode acessar recursos
 * próprios Utilizada em endpoints onde USER só deve acessar seus próprios dados
 *
 * Exemplo de uso:
 *
 * @ValidateResourceOwnership(resourceType = "reservation", userIdParam =
 * "userId") public ResponseEntity<ReservationDTO>
 * getMyReservation(@PathVariable UUID userId)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateResourceOwnership {

    /**
     * Tipo do recurso sendo acessado
     */
    String resourceType() default "";

    /**
     * Nome do parâmetro que contém o ID do usuário proprietário do recurso
     */
    String userIdParam() default "userId";

    /**
     * Indica se ATENDENTE também pode acessar este recurso
     */
    boolean allowAtendente() default true;

    /**
     * Mensagem de erro personalizada
     */
    String message() default "Acesso negado: você só pode acessar seus próprios recursos";
}
