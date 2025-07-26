package com.orvian.travelapi.service.impl;

import com.orvian.travelapi.controller.dto.user.CreateUserDTO;
import com.orvian.travelapi.controller.dto.user.UpdateUserDTO;
import com.orvian.travelapi.controller.dto.user.UserSearchResultDTO;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.mapper.UserMapper;
import com.orvian.travelapi.service.UserService;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import com.orvian.travelapi.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
       O método getAllUsers() recupera todos os usuários do repositório,
       converte cada usuário em um UserSearchResultDTO e retorna a lista.
     */
    @Override
    public List<UserSearchResultDTO> findAll() {
        log.info("Retrieving all users");
        List<User> userList = userRepository.findAll();
        return userMapper.toUserSearchResultDTOList(userList);
    }

    /*
         O método create() cria um novo usuário.
         Ele valida o usuário antes de salvá-lo no repositório e retorna o usuário salvo.
         Se o usuário já existir, lança uma DuplicatedRegistryException.
     */
    @Override
    public User create(Record dto) {
        User user = userMapper.toEntity((CreateUserDTO) dto);
        log.info("Creating user with email: {}", user.getEmail());
        validateCreationAndUpdate(user);
        return userRepository.save(user);
    }

    /**
     O método getUserById() busca um usuário pelo ID.
     Se o usuário for encontrado, retorna um Optional contendo o usuário.
     Caso contrário, retorna um Optional vazio.
     */
    @Override
    public UserSearchResultDTO findById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id + " not found."));
        log.info("User found with id: {}", id);
        return userMapper.toDTO(user);
    }

    /*
         O método update() atualiza um usuário existente.
         Ele valida o usuário antes de salvá-lo no repositório e retorna o usuário atualizado.
         Se o usuário já existir, lança uma DuplicatedRegistryException.
     */
    @Override
    public void update(UUID id, Record dto) {
        Optional<User> userOptional = userRepository.findById(id); // Busca o usuário pelo ID
        if (userOptional.isEmpty()) {
            log.error("User with id {} not found", id);
            throw new NotFoundException("User with id " + id + " not found."); // Retorna 404 se o usuário não for encontrado
        }

        User user = userOptional.get(); // Obtém o usuário encontrado
        log.info("Updating user with ID: {}", user.getId());
        validateCreationAndUpdate(user);

        userMapper.updateEntityFromDto((UpdateUserDTO) dto, user); // Atualiza o usuário com os dados do DTO, ignorando valores nulos

        User updatedUser = userRepository.save(user);
        log.info("User updated with ID: {}", updatedUser.getId());
    }

    /*
         O método delete() remove um usuário do repositório pelo ID.
         Ele registra a ação de exclusão e confirma a exclusão bem-sucedida.
     */
    @Override
    public void delete(UUID id) {
        Optional<User> userOptional = userRepository.findById(id); // Busca o usuário pelo ID

        if (userOptional.isEmpty()) {
            log.error("User with id {} not found", id);
            throw new NotFoundException("User with id " + id + " not found."); // Retorna 404 (Not Found) se o usuário não for encontrado
        }

        userRepository.deleteById(id);
        log.info("User with ID: {} deleted successfully", id);
    }

    public Page<UserSearchResultDTO> userPage(Integer pageNumber, Integer pageSize) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<User> userEntitiesPage = userRepository.findAll(pageRequest);
        return userEntitiesPage.map(userMapper::toDTO);
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
}
