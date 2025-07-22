package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.error.ResponseErrorDTO;
import com.orvian.travelapi.controller.dto.traveler.CreateTravelerDTO;
import com.orvian.travelapi.controller.dto.traveler.TravelerSearchResultDTO;
import com.orvian.travelapi.controller.dto.traveler.UpdateTravelerDTO;
import com.orvian.travelapi.domain.model.Traveler;
import com.orvian.travelapi.mapper.TravelerMapper;
import com.orvian.travelapi.service.impl.TravelerServiceImpl;
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

@RestController
@RequestMapping("/api/v1/travelers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gerenciamento de Viajantes", description = "Operações relacionadas ao gerenciamnento de viajante")
public class TravelerControllerImpl implements GenericController {

    private final TravelerServiceImpl travelerService;
    private final TravelerMapper travelerMapper;

    @PostMapping
    @Operation(summary = "Criar um novo Viajante", description = "Cria um novo Viajante com as credenciais oferecidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Viajante criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Viajante com as mesmas credenciais ja existente", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> createTraveler(@Valid @RequestBody CreateTravelerDTO dto){
        log.info("Creating traveler with details: {}", dto);
        Traveler createdTraveler = travelerService.create(dto);
        log.info("Traveler created with ID {}", createdTraveler.getId());
        URI location = generateHeaderLocation(createdTraveler.getId());
        return  ResponseEntity.created(location).build();
    }

    @GetMapping
    @Operation(summary = "Buscar todos os Viajantes", description = "Recupera a lista de todos os Viajantes registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Viajantes recuperados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<List<TravelerSearchResultDTO>> getAllTravelers(){
        log.info("Fetching all travelers");
        List<TravelerSearchResultDTO> travelers = travelerService.findAll();
        log.info("total travelers found: {}", travelers.size());
        return ResponseEntity.ok(travelers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um viajante pelo ID", description = "Recupera um viajante identificando pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Viajante encontrado"),
            @ApiResponse(responseCode = "404", description = "Viajante não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<TravelerSearchResultDTO> getTravelerById(@PathVariable UUID id){
        return ResponseEntity.ok(travelerService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um Viajante existente", description = "Atualiza as credenciais de um usuário existente, identificando pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Viajante atualizado ocm sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Viajante não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Viajante com as mesmas credenciais ja existente", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> updateTraveler(@PathVariable UUID id, @Valid @RequestBody UpdateTravelerDTO dto){
        log.info("updating traveler with ID: {}", id);
        travelerService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um viajante", description = "Exclui um viajante identificando pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "viajante excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "viajante não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> deleteTraveler(@PathVariable UUID id){
        log.info("Deleting traveler with ID: {}", id);
        travelerService.delete(id);
        log.info("Traveler deleted successfully");
        return ResponseEntity.noContent().build();
    }

    //todo metodo para buscar por ID da reserva como opção extra
}
