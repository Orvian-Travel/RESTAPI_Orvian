package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.error.ResponseErrorDTO;
import com.orvian.travelapi.controller.dto.user.CreateUserDTO;
import com.orvian.travelapi.controller.dto.user.UpdateUserDTO;
import com.orvian.travelapi.controller.dto.user.UserSearchResultDTO;
import com.orvian.travelapi.domain.model.User;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

//TODO: refatorar o controller para centralizar a lógica na camada de serviço.

/*
    Controller padrão para gerenciar usuários no sistema.
    Este controller permite criar, atualizar, buscar e deletar usuários.
    @Sl4j é usado para registrar logs de atividades.
    @Tag é usado para categorizar as operações na documentação OpenAPI com swagger.
 */
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Operations related to user management")
public class UserControllerImpl implements GenericController {

    private final UserServiceImpl userService;

    /*
        Cria um novo usuário com as credenciais fornecidas.
        @PostMapping é usado para mapear requisições HTTP POST para este método.
        @Operation e @ApiResponses são usados para documentar a operação na API.
        @Valid é usado para validar o DTO de entrada.
        Retorna um ResponseEntity com o status 201 (Created) e a URI do novo usuário no cabeçalho Location.
        Erro 400 (Bad Request) é retornado se os dados de entrada forem inválidos.
        Erro 409 (Conflict) é retornado se já existir um usuário com as mesmas credenciais.
        Erro 500 (Internal Server Error) é retornado em caso de erro no servidor.
     */
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided credentials.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "User with the same credentials already exists", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> createUser(@RequestBody @Valid CreateUserDTO dto) {
        log.info("Creating user with email: {}", dto.email());
        User user = userService.create(dto);
        URI location = generateHeaderLocation(user.getId()); // Gera a URI do novo usuário
        return ResponseEntity.created(location).build(); // Retorna o status 201 (Created) com a URI no cabeçalho Location
    }

    /*
        Busca todos os usuários registrados no sistema.
        @GetMapping é usado para mapear requisições HTTP GET para este método.
        @Operation e @ApiResponses são usados para documentar a operação na API.
        Retorna um ResponseEntity com uma lista de UserSearchResultDTO e o status 200 (OK).
        Erro 500 (Internal Server Error) é retornado em caso de erro no servidor.
     */
    @GetMapping
    @Operation(summary = "Find all users", description = "Retrieves a list of all registered users.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<List<UserSearchResultDTO>> getAllUsers() {
        log.info("Fetching all users");
        List<UserSearchResultDTO> dtoList = userService.findAll();
        log.info("Total users found: {}", dtoList.size());
        return ResponseEntity.ok(dtoList); // Retorna a lista de usuários com status 200 (OK)
    }

    /*
        Busca um usuário específico pelo ID.
        @GetMapping("/{id}") é usado para mapear requisições HTTP GET para este método, onde {id} é o ID do usuário.
        @PathVariable é usado para extrair o ID do usuário da URL.
        @Operation e @ApiResponses são usados para documentar a operação na API.
        Retorna um ResponseEntity com o UserSearchResultDTO e o status 200 (OK) se o usuário for encontrado.
        Erro 404 (Not Found) é retornado se o usuário não for encontrado.
        Erro 500 (Internal Server Error) é retornado em caso de erro no servidor.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Find a user by ID", description = "Retrieves a user identified by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<UserSearchResultDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /*
        Atualiza um usuário existente com as credenciais fornecidas.
        @PutMapping é usado para mapear requisições HTTP PUT para este método.
        @PathVariable é usado para extrair o ID do usuário da URL.
        @Valid é usado para validar o DTO de entrada.
        Retorna um ResponseEntity com o status 204 (No Content) se a atualização for bem-sucedida.
        Erro 400 (Bad Request) é retornado se os dados de entrada forem inválidos.
        Erro 404 (Not Found) é retornado se o usuário não for encontrado.
        Erro 409 (Conflict) é retornado se já existir um usuário com as mesmas credenciais.
        Erro 500 (Internal Server Error) é retornado em caso de erro no servidor.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user", description = "Updates the credentials of an existing user identified by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "User with the same credentials already exists", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody @Valid UpdateUserDTO dto) {
        log.info("Updating user with id: {}", id);
        userService.update(id, dto);
        return ResponseEntity.noContent().build(); // Retorna 204 (No Content) se a atualização for bem-sucedida
    }

    /*
        Deleta um usuário identificado pelo ID.
        @DeleteMapping("/{id}") é usado para mapear requisições HTTP DELETE para este método, onde {id} é o ID do usuário.
        @PathVariable é usado para extrair o ID do usuário da URL.
        @Operation e @ApiResponses são usados para documentar a operação na API.
        Retorna um ResponseEntity com o status 204 (No Content) se a exclusão for bem-sucedida.
        Erro 404 (Not Found) é retornado se o usuário não for encontrado.
        Erro 500 (Internal Server Error) é retornado em caso de erro no servidor.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user identified by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Deleting user with id: {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build(); // Retorna 204 (No Content) se a exclusão for bem-sucedida
    }
}
