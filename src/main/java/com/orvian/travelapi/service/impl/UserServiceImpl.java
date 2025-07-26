package com.orvian.travelapi.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.orvian.travelapi.controller.dto.user.CreateUserDTO;
import com.orvian.travelapi.controller.dto.user.UpdateUserDTO;
import com.orvian.travelapi.controller.dto.user.UserSearchResultDTO;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.mapper.UserMapper;
import com.orvian.travelapi.service.UserService;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import com.orvian.travelapi.service.exception.NotFoundException;
import static com.orvian.travelapi.specs.UserSpecs.nameLike;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Page<UserSearchResultDTO> findAll(Integer pageNumber, Integer pageSize, String name) {
        Specification<User> spec = (name != null && !name.isBlank()) ? nameLike(name) : null;

        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        Page<User> userEntitiesPage = userRepository.findAll(spec, pageRequest);
        return userEntitiesPage.map(userMapper::toDTO);
    }

    @Override
    public User create(Record dto) {
        User user = userMapper.toEntity((CreateUserDTO) dto);
        log.info("Creating user with email: {}", user.getEmail());
        validateCreationAndUpdate(user);
        return userRepository.save(user);
    }

    @Override
    public UserSearchResultDTO findById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id + " not found."));
        log.info("User found with id: {}", id);
        return userMapper.toDTO(user);
    }

    @Override
    public void update(UUID id, Record dto) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            log.error("User with id {} not found", id);
            throw new NotFoundException("User with id " + id + " not found.");
        }

        User user = userOptional.get();
        log.info("Updating user with ID: {}", user.getId());
        validateCreationAndUpdate(user);

        userMapper.updateEntityFromDto((UpdateUserDTO) dto, user);

        User updatedUser = userRepository.save(user);
        log.info("User updated with ID: {}", updatedUser.getId());
    }

    @Override
    public void delete(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            log.error("User with id {} not found", id);
            throw new NotFoundException("User with id " + id + " not found.");
        }

        userRepository.deleteById(id);
        log.info("User with ID: {} deleted successfully", id);
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
