package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.*;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.mapper.UserMapper;
import com.orvian.travelapi.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Slf4j
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Operations related to user management")
public class UserControllerImpl implements GenericController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "User with the same email already exists", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> create(@RequestBody @Valid CreateUserDTO dto) {
        User user = userMapper.toEntity(dto);
        log.info("Creating user with email: {}", user.getEmail());
        userService.create(user);

        URI location = gerarHeaderLocation(user.getId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user", description = "Updates the details of an existing user identified by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "User with the same details already exists", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
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
    @Operation(summary = "Find all users", description = "Retrieves a list of all registered users.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<UsersListResponseDTO> findAll() {
        log.info("Fetching all users");
        UsersListResponseDTO dtoList = userService.findAll();
        log.info("Total users found: {}", dtoList.usersList().size());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a user by ID", description = "Retrieves a user identified by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<UserSearchResultDTO> findById(@PathVariable UUID id) {

        return userService.findById(id)
                .map(user -> {
                    UserSearchResultDTO dto = userMapper.toDTO(user);
                    log.info("User found with id: {}", id);
                    return ResponseEntity.ok(dto);
                }).orElseGet( () -> {
                    log.error("User with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user identified by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
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
