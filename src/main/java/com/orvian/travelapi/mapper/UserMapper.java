package com.orvian.travelapi.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.orvian.travelapi.controller.dto.user.CreateUserDTO;
import com.orvian.travelapi.controller.dto.user.UpdateUserDTO;
import com.orvian.travelapi.controller.dto.user.UserSearchResultDTO;
import com.orvian.travelapi.domain.model.User;

/*
    Mapper para a entidade de usuário.
    @Mapper é uma anotação do MapStruct que gera a implementação da interface em tempo de compilação.
    O componenteModel = "spring" permite que o MapStruct gere um bean Spring, facilitando a injeção de dependências.
    O @BeanMapping com nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE serve para ignorar os valores nulos na hora de fazer o mapeamento de atualização.
 */
@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface UserMapper {

    User toEntity(CreateUserDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateUserDTO dto, @MappingTarget User user);

    UserSearchResultDTO toDTO(User user);

    List<UserSearchResultDTO> toUserSearchResultDTOList(List<User> userList);
}
