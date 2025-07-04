package com.orvian.travelapi.mapper;

import com.orvian.travelapi.controller.dto.CreateUserDTO;
import com.orvian.travelapi.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(CreateUserDTO dto);
}
