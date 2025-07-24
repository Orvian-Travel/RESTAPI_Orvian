package com.orvian.travelapi.controller;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/*
    Esse controller é utilizado para fornecer uma implementação genérica de um método comum para gerar URIs de localização no cabeçalho HTTP.
    Será utilizado em todos os controllers.
 */
public interface GenericController {

    default URI generateHeaderLocation(UUID id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
