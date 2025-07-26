package com.orvian.travelapi.controller.impl;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
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
import com.orvian.travelapi.controller.dto.PageResponseDTO;
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
@Tag(name = "Gerenciamento de Usuário", description = "Operações relacionadas ao gerenciamnento de usuário")
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
        log.info("User created with ID: {}", user.getId());// Gera a URI do novo usuário

        UserSearchResultDTO createdUserDTO = userService.findById(user.getId());

        return ResponseEntity.created(location).body(createdUserDTO); // Retorna o status 201 (Created) com a URI no cabeçalho Location
    }

    /*
        Busca todos os usuários registrados no sistema.
        @GetMapping é usado para mapear requisições HTTP GET para este método.
        @Operation e @ApiResponses são usados para documentar a operação na API.
        Retorna um ResponseEntity com uma lista de UserSearchResultDTO e o status 200 (OK).
        Erro 500 (Internal Server Error) é retornado em caso de erro no servidor.
     */
    @GetMapping
    @Operation(summary = "Paginação da lista de todos os usuários", description = "Recupera uma página contendo uma lista de usuários.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuários recuperados com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<PageResponseDTO<UserSearchResultDTO>> getUsersByPage(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {

        Page<UserSearchResultDTO> page = userService.findAll(pageNumber, pageSize, name);

        PageResponseDTO<UserSearchResultDTO> response = new PageResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );

        return ResponseEntity.ok(response);
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
    @Operation(summary = "Buscar um usuário pelo ID", description = "Recupera um usuário identificando pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
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
    @Operation(summary = "Atualiza um usuário existente", description = "Atualiza as credenciais de um usuário existente, identificando pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário atualizado ocm sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "409", description = "Usuário com as mesmas credenciais ja existente", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
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
    @Operation(summary = "Excluir um usuário", description = "Exclui um usuário identificando pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Deleting user with id: {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build(); // Retorna 204 (No Content) se a exclusão for bem-sucedida
    }
}
