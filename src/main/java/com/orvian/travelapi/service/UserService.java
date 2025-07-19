package com.orvian.travelapi.service;

import com.orvian.travelapi.controller.dto.user.UpdateUserDTO;
import com.orvian.travelapi.domain.model.User;

import java.util.UUID;

public interface UserService extends CrudService<UUID, User> {
    /*
             O método update() atualiza um usuário existente.
             Ele valida o usuário antes de salvá-lo no repositório e retorna o usuário atualizado.
             Se o usuário já existir, lança uma DuplicatedRegistryException.
         */

}
