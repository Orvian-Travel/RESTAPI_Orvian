package com.orvian.travelapi.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.orvian.travelapi.controller.dto.user.UserSearchResultDTO;
import com.orvian.travelapi.domain.model.User;

public interface UserService extends CrudService<UUID, User> {
    // ✅ Sobrescrever com tipos específicos

    @Override
    Page<UserSearchResultDTO> findAll(Integer pageNumber, Integer pageSize, String name);

    @Override
    UserSearchResultDTO findById(UUID id);
}
