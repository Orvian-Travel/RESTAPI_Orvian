package com.orvian.travelapi.service;

import java.util.UUID;

import com.orvian.travelapi.domain.model.User;

public interface UserService extends CrudService<UUID, User> {
    /*
     * O método update() atualiza um usuário existente.
     * Ele valida o usuário antes de salvá-lo no repositório e retorna o usuário
     * atualizado.
     * Se o usuário já existir, lança uma DuplicatedRegistryException.
     */
}
