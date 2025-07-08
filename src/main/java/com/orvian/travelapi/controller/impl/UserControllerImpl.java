package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.CreateUserDTO;
import com.orvian.travelapi.controller.dto.ResponseErrorDTO;
import com.orvian.travelapi.controller.dto.UpdateUserDTO;
import com.orvian.travelapi.controller.dto.UserSearchResultDTO;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.mapper.UserMapper;
import com.orvian.travelapi.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements GenericController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateUserDTO dto) {
        User user = userMapper.toEntity(dto);
        log.info("Creating user with email: {}", user.getEmail());
        userService.create(user);

        URI location = gerarHeaderLocation(user.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable UUID id, @RequestBody @Valid UpdateUserDTO dto) {
        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isEmpty()) {
            log.error("User with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                            new ResponseErrorDTO(
                                    HttpStatus.NOT_FOUND.value(),
                                    "User not found",
                                    List.of())
                    );
        }

        User user = userOptional.get();
        userMapper.updateEntityFromDto(dto, user);

        log.info("Updating user with id: {}", user.getId());
        userService.update(user);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        log.info("Fetching all users");
        List<User> users = userService.findAll();
        log.info("Total users found: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSearchResultDTO> findById(@PathVariable UUID id) {

        return userService.findById(id)
                .map(user -> {
                    UserSearchResultDTO dto = userMapper.toDTO(user);
                    log.info("User found with id: {}", id);
                    return ResponseEntity.ok(dto);
                }).orElseGet( () -> {
                    log.error("User with id {} not found", id);
                    ResponseEntity.notFound().build())
                };
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isEmpty()) {
            log.error("User with id {} not found", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Deleting user with id: {}", id);
        userService.delete(id);
        log.info("User with id {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
