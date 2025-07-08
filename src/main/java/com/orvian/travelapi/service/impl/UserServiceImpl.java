package com.orvian.travelapi.service.impl;

import com.orvian.travelapi.controller.dto.UserSearchResultDTO;
import com.orvian.travelapi.controller.dto.UsersListResponseDTO;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.mapper.UserMapper;
import com.orvian.travelapi.service.UserService;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UsersListResponseDTO findAll() {
        log.info("Retrieving all users");
        List<User> userList = userRepository.findAll();
        List<UserSearchResultDTO> dtoList = userList.stream()
                .map(userMapper::toDTO)
                .toList();
        return new UsersListResponseDTO(dtoList);
    }

    @Override
    public Optional<User> findById(UUID id) {
        log.info("Retrieving user with ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public User create(User user) {
        log.info("Creating user with email: {}", user.getEmail());
        validateCreationAndUpdate(user);
        User savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    public User update(User user) {
        log.info("Updating user with ID: {}", user.getId());
        validateCreationAndUpdate(user);
        User updatedUser = userRepository.save(user);
        log.info("User updated with ID: {}", updatedUser.getId());
        return updatedUser;
    }

    @Override
    public void delete(UUID uuid) {
        log.info("Deleting user with ID: {}", uuid);
        userRepository.deleteById(uuid);
        log.info("User with ID: {} deleted successfully", uuid);
    }

    private void validateCreationAndUpdate(User user) {
        if (isDuplicateUser(user)) {
            log.error("User already registered: {}", user);
            throw new DuplicatedRegistryException("User already registered");
        }
    }

    private boolean isDuplicateUser(User user) {
        Optional<User> userOptional = userRepository.findByEmailOrDocumentOrPhone(user.getEmail(), user.getDocument(), user.getPhone());

        if (user.getId() == null) {
            return userOptional.isPresent();
        }

        return !user.getId().equals(userOptional.get().getId()) && userOptional.isPresent();
    }
}
