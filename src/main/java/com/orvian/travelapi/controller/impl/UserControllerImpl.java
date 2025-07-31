package com.orvian.travelapi.controller.impl;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.error.ResponseErrorDTO;
import com.orvian.travelapi.controller.dto.user.CreateUserDTO;
import com.orvian.travelapi.controller.dto.user.UpdateUserDTO;
import com.orvian.travelapi.controller.dto.user.UserSearchResultDTO;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.service.exception.AccessDeniedException;
import com.orvian.travelapi.service.impl.UserServiceImpl;
import com.orvian.travelapi.service.security.OrvianAuthorizationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Gerenciamento de Usuário", description = "Operações relacionadas ao gerenciamnento de usuário")
public class UserControllerImpl implements GenericController {

    private final UserServiceImpl userService;

    private final PagedResourcesAssembler<UserSearchResultDTO> pagedResourcesAssembler;

    private final OrvianAuthorizationService authorizationService;

    @PostMapping
    @Operation(summary = "Criar um novo Usuário", description = "Cria um novo usuário com as credenciais oferecidas.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "409", description = "Usuário com as mesmas credenciais ja existente", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<UserSearchResultDTO> createUser(@RequestBody @Valid CreateUserDTO dto) {
        log.info("Creating user with email: {}", dto.email());
        User user = userService.create(dto);
        URI location = generateHeaderLocation(user.getId());
        log.info("User created with ID: {}", user.getId());

        UserSearchResultDTO createdUserDTO = userService.findById(user.getId());

        return ResponseEntity.created(location).body(createdUserDTO);
    }

    @GetMapping
    @Operation(summary = "Paginação da lista de todos os usuários", description = "Recupera uma página contendo uma lista de usuários.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuários recuperados com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<PagedModel<EntityModel<UserSearchResultDTO>>> getUsersByPage(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {

        if (!authorizationService.isCurrentUserAttendenteOrAdmin()) {
            throw new AccessDeniedException("Apenas administradores e atendentes podem listar usuários");
        }

        Page<UserSearchResultDTO> page = userService.findAll(pageNumber, pageSize, name);
        PagedModel<EntityModel<UserSearchResultDTO>> pagedModel = pagedResourcesAssembler.toModel(page);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um usuário pelo ID", description = "Recupera um usuário identificando pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<UserSearchResultDTO> getUserById(@PathVariable UUID id) {
        if (!authorizationService.canAccessUserData(id)) {
            throw new AccessDeniedException("Você não tem permissão para acessar dados deste usuário");
        }
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuário existente", description = "Atualiza as credenciais de um usuário existente, identificando pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário atualizado ocm sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "409", description = "Usuário com as mesmas credenciais ja existente", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody @Valid UpdateUserDTO dto) {

        if (!authorizationService.canUpdateUser(id)) {
            throw new AccessDeniedException("Você não tem permissão para atualizar este usuário");
        }

        log.info("Updating user with id: {}", id);
        userService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um usuário", description = "Exclui um usuário identificando pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        if (!authorizationService.canModifyResource("DELETE", "user")) {
            throw new AccessDeniedException("Apenas administradores podem excluir usuários");
        }

        log.info("Deleting user with id: {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
