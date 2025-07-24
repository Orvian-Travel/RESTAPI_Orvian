package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.error.ResponseErrorDTO;
import com.orvian.travelapi.controller.dto.travelpackage.CreateTravelPackageDTO;
import com.orvian.travelapi.controller.dto.travelpackage.PackageSearchResultDTO;
import com.orvian.travelapi.controller.dto.travelpackage.UpdateTravelPackageDTO;
import com.orvian.travelapi.domain.model.TravelPackage;
import com.orvian.travelapi.service.impl.PackageServiceImpl;
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
@RequestMapping("/api/v1/packages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gerenciamento de Pacotes", description = "Endpoints para gerenciamento de pacotes de viagem")
public class TravelPackageControllerImpl implements GenericController {

    private final PackageServiceImpl packageService;

    @PostMapping
    @Operation(summary = "Criar um novo pacote de viagem", description = "Cria um novo pacote de viagem com os detalhes fornecidos.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pacote de viagem criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "409", description = "Já existe um pacote com os mesmos dados", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> createPackage(@Valid @RequestBody CreateTravelPackageDTO dto) {
        log.info("Creating new travel package with details: {}", dto);
        TravelPackage createdPackage = packageService.create(dto);
        log.info("Travel package created successfully with ID: {}", createdPackage.getId());
        URI location = generateHeaderLocation(createdPackage.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @Operation(summary = "Listar todos os pacotes de viagem", description = "Busca uma lista de todos os pacotes de viagem disponíveis.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pacotes de viagem recuperados com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<List<PackageSearchResultDTO>> getAllPackages() {
        log.info("Fetching all packages");
        List<PackageSearchResultDTO> packages = packageService.findAll();
        log.info("Total Packages found: {}", packages.size());
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um pacote de viagem por ID", description = "Busca um pacote de viagem identificado pelo seu ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pacote de viagem encontrado"),
        @ApiResponse(responseCode = "404", description = "Pacote de viagem não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<PackageSearchResultDTO> getPackageById(@PathVariable UUID id) {
        return ResponseEntity.ok(packageService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um pacote de viagem", description = "Atualiza os detalhes de um pacote de viagem existente identificado pelo seu ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pacote atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pacote não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "409", description = "Já existe um pacote com os mesmos dados", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> updatePackage(@PathVariable UUID id, @Valid @RequestBody UpdateTravelPackageDTO dto) {
        log.info("Updating Package with id: {}", id);
        packageService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um pacote de viagem", description = "Exclui um pacote de viagem identificado pelo seu ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pacote de viagem excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pacote de viagem não encontrado", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> deletePackage(@PathVariable UUID id) {
        log.info("Deleting travel package with ID: {}", id);
        packageService.delete(id);
        log.info("Travel package deleted successfully");
        return ResponseEntity.noContent().build();
    }
}
