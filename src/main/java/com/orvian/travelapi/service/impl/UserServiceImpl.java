package com.orvian.travelapi.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.orvian.travelapi.controller.dto.user.CreateUserDTO;
import com.orvian.travelapi.controller.dto.user.UpdateUserDTO;
import com.orvian.travelapi.controller.dto.user.UserSearchResultDTO;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.mapper.UserMapper;
import com.orvian.travelapi.service.UserService;
import com.orvian.travelapi.service.exception.AccessDeniedException;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import com.orvian.travelapi.service.exception.NotFoundException;
import static com.orvian.travelapi.service.exception.PersistenceExceptionUtil.handlePersistenceError;
import static com.orvian.travelapi.specs.UserSpecs.nameLike;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    @Override
    public Page<UserSearchResultDTO> findAll(Integer pageNumber, Integer pageSize, String name) {
        try {
            Specification<User> spec = (name != null && !name.isBlank()) ? nameLike(name) : null;

            Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
            Page<User> userEntitiesPage = userRepository.findAll(spec, pageRequest);
            return userEntitiesPage.map(userMapper::toDTO);
        } catch (Exception e) {
            log.error("Erro ao buscar reservas: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar usuários: " + e.getMessage());
        }
    }

    @Override
    public User create(Record dto) {
        User user = userMapper.toEntity((CreateUserDTO) dto);
        log.info("Creating user with email: {} and requested role: {}", user.getEmail(), user.getRole());

        // ✅ Verificar se o usuário atual pode atribuir a role solicitada
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // ✅ Usuário autenticado - verificar permissões
            User currentUser = userRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new AccessDeniedException("Usuário autenticado não encontrado"));

            String currentUserRole = currentUser.getRole();
            String requestedRole = user.getRole();

            if ("ADMIN".equals(currentUserRole)) {
                // ✅ ADMIN pode criar usuário com qualquer role
                log.info("ADMIN {} creating user with role: {}", currentUser.getEmail(), requestedRole);

                // Validar se a role é válida
                if (!isValidRole(requestedRole)) {
                    log.warn("Invalid role requested: {}", requestedRole);
                    throw new IllegalArgumentException("Role inválida: " + requestedRole + ". Roles válidas: USER, ATENDENTE, ADMIN");
                }
            } else {
                // ✅ Usuário não-ADMIN (USER ou ATENDENTE) só pode criar com role USER
                log.info("Non-admin user {} attempting to create user, forcing role to USER", currentUser.getEmail());
                user.setRole("USER");
            }
        } else {
            // ✅ Usuário não autenticado - registro público sempre como USER
            log.info("Public registration for email: {}, forcing role to USER", user.getEmail());
            user.setRole("USER");
        }

        validateCreationAndUpdate(user);
        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {} and final role: {}", savedUser.getId(), savedUser.getRole());
        return savedUser;
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

        try {
            User user = userOptional.get();
            UpdateUserDTO updateDto = (UpdateUserDTO) dto;

            log.info("Updating user with ID: {} - Current role: {}, Requested role: {}",
                    user.getId(), user.getRole(), updateDto.role());

            // ✅ NOVO CONTROLE: Verificar permissões para alteração de role
            if (updateDto.role() != null && !updateDto.role().equals(user.getRole())) {
                // Role está sendo alterada - verificar permissões
                handleRoleUpdatePermissions(user, updateDto.role());
            }

            validateCreationAndUpdate(user);

            // ✅ Aplicar as atualizações via mapper
            userMapper.updateEntityFromDto(updateDto, user);

            // ✅ Se a senha foi fornecida, criptografar
            if (updateDto.password() != null && !updateDto.password().isBlank()) {
                user.setPassword(encoder.encode(updateDto.password()));
                log.info("Password updated for user: {}", user.getEmail());
            }

            User updatedUser = userRepository.save(user);
            log.info("User updated successfully - ID: {}, Final role: {}", updatedUser.getId(), updatedUser.getRole());

        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for user update: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid argument provided for user update: " + e.getMessage());
        } catch (RuntimeException e) {
            handlePersistenceError(e, log);
        }
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

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void resetUserPassword(String userEmail, String newPassword) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User with email " + userEmail + " not found."));

        if (encoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the current one.");
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
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

    private boolean isValidRole(String role) {
        return role != null && ("USER".equals(role) || "ATENDENTE".equals(role) || "ADMIN".equals(role));
    }

    private void handleRoleUpdatePermissions(User targetUser, String newRole) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            log.warn("Unauthenticated user trying to update role from {} to {}",
                    targetUser.getRole(), newRole);
            throw new AccessDeniedException("Usuário não autenticado não pode alterar roles");
        }

        String currentUserEmail = auth.getName();

        // ✅ Buscar o usuário atual
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new AccessDeniedException("Usuário autenticado não encontrado"));

        String currentUserRole = currentUser.getRole();

        if (!"ADMIN".equals(currentUserRole)) {
            // ✅ Apenas ADMIN pode alterar roles
            log.warn("Non-admin user {} (role: {}) trying to change role from {} to {} for user: {}",
                    currentUser.getEmail(), currentUserRole, targetUser.getRole(), newRole, targetUser.getEmail());

            throw new AccessDeniedException("Apenas administradores podem alterar roles de usuários");
        }

        // ✅ Validar se a nova role é válida
        if (!isValidRole(newRole)) {
            log.warn("ADMIN {} trying to set invalid role: {}", currentUser.getEmail(), newRole);
            throw new IllegalArgumentException("Role inválida: " + newRole + ". Roles válidas: USER, ATENDENTE, ADMIN");
        }

        log.info("ADMIN {} changing role from {} to {} for user: {}",
                currentUser.getEmail(), targetUser.getRole(), newRole, targetUser.getEmail());
    }
}
