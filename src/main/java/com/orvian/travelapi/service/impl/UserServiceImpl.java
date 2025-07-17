package com.orvian.travelapi.service.impl;

import com.orvian.travelapi.controller.dto.user.CreateUserDTO;
import com.orvian.travelapi.controller.dto.user.UserSearchResultDTO;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.mapper.UserMapper;
import com.orvian.travelapi.service.UserService;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
       O UserServiceImpl é a implementação da interface UserService.
       @Slf4j é usado para logging, e RequiredArgsConstructor para injeção de dependências.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /*
       O método findAll() recupera todos os usuários do repositório,
       converte cada usuário em um UserSearchResultDTO e retorna a lista.
     */
    @Override
    public List<UserSearchResultDTO> findAll() {
        log.info("Retrieving all users");
        List<User> userList = userRepository.findAll();
        return userMapper.toUserSearchResultDTOList(userList);
    }


    /**
         O método findById() busca um usuário pelo ID.
        Se o usuário for encontrado, retorna um Optional contendo o usuário.
        Caso contrário, retorna um Optional vazio.
     */
    @Override
    public Optional<User> findById(UUID id) {
        log.info("Retrieving user with ID: {}", id);
        return userRepository.findById(id);
    }

    /*
         O método create() cria um novo usuário.
         Ele valida o usuário antes de salvá-lo no repositório e retorna o usuário salvo.
         Se o usuário já existir, lança uma DuplicatedRegistryException.
     */
    @Override
    public User create(User user) {
        log.info("Creating user with email: {}", user.getEmail());
        validateCreationAndUpdate(user);
        User savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());
        return savedUser;
    }

    /*
         O método update() atualiza um usuário existente.
         Ele valida o usuário antes de salvá-lo no repositório e retorna o usuário atualizado.
         Se o usuário já existir, lança uma DuplicatedRegistryException.
     */
    @Override
    public User update(User user) {
        log.info("Updating user with ID: {}", user.getId());
        validateCreationAndUpdate(user);
        User updatedUser = userRepository.save(user);
        log.info("User updated with ID: {}", updatedUser.getId());
        return updatedUser;
    }

    /*
         O método delete() remove um usuário do repositório pelo ID.
         Ele registra a ação de exclusão e confirma a exclusão bem-sucedida.
     */
    @Override
    public void delete(UUID uuid) {
        log.info("Deleting user with ID: {}", uuid);
        userRepository.deleteById(uuid);
        log.info("User with ID: {} deleted successfully", uuid);
    }

    /*
         O método validateCreationAndUpdate() verifica se o usuário já existe
         antes de criar ou atualizar. Se o usuário já estiver registrado, lança uma DuplicatedRegistryException.
         Ele utiliza o método isDuplicateUser() para verificar a duplicidade.
     */
    private void validateCreationAndUpdate(User user) {
        if (isDuplicateUser(user)) {
            log.error("User already registered: {}", user);
            throw new DuplicatedRegistryException("User already registered");
        }
    }

    /*
         O método isDuplicateUser() verifica se o usuário já existe no repositório
         com base no email, documento ou telefone. Se o usuário já existir,
         retorna true; caso contrário, retorna false.
     */
    private boolean isDuplicateUser(User user) {
        Optional<User> userOptional = userRepository.findByEmailOrDocumentOrPhone(user.getEmail(), user.getDocument(), user.getPhone());

        if (user.getId() == null) {
            return userOptional.isPresent();
        }

        return !user.getId().equals(userOptional.get().getId()) && userOptional.isPresent();
    }

    public User create(CreateUserDTO dto) {
        User user = userMapper.toEntity(dto);
        log.info("Creating user with email: {}", user.getEmail());
        validateCreationAndUpdate(user);
        log.info("User created with ID: {}", user.getId());
        return userRepository.save(user);
    }
}
