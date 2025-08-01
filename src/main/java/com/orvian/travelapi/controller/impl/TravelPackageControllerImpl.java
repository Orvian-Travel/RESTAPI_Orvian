package com.orvian.travelapi.controller.impl;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
import com.orvian.travelapi.controller.dto.travelpackage.CreateTravelPackageDTO;
import com.orvian.travelapi.controller.dto.travelpackage.PackageSearchResultDTO;
import com.orvian.travelapi.controller.dto.travelpackage.UpdateTravelPackageDTO;
import com.orvian.travelapi.domain.model.TravelPackage;
import com.orvian.travelapi.service.TravelPackageService;

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
@RequestMapping("/api/v1/packages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gerenciamento de Pacotes", description = "Endpoints para gerenciamento de pacotes de viagem")
public class TravelPackageControllerImpl implements GenericController {

    private final TravelPackageService packageService;

    private final PagedResourcesAssembler<PackageSearchResultDTO> pagedResourcesAssembler;

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
    @Operation(summary = "Paginação da lista de todos os Pacotes", description = "Recupera uma página contendo uma lista de usuários.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pacotes recuperados com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<PagedModel<EntityModel<PackageSearchResultDTO>>> getPackagesByPage(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title) {

        Page<PackageSearchResultDTO> page = packageService.findAll(pageNumber, pageSize, title);
        PagedModel<EntityModel<PackageSearchResultDTO>> pagedModel = pagedResourcesAssembler.toModel(page);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/search")
    @Operation(summary = "Paginação da lista de todos os Pacotes", description = "Recupera uma página contendo uma lista de usuários.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pacotes recuperados com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<PagedModel<EntityModel<PackageSearchResultDTO>>> getPackagesBySearch(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = true) String title,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(defaultValue = "1") Integer maxPeople
    ) {
        LocalDate effectiveStartDate = startDate != null ? startDate : LocalDate.now(ZoneOffset.UTC);
        log.info("Searching packages with title: {}, startDate: {}, maxPeople: {}", title, effectiveStartDate, maxPeople);

        Page<PackageSearchResultDTO> page = packageService.findAllBySearch(pageNumber, pageSize, title, effectiveStartDate, maxPeople);
        PagedModel<EntityModel<PackageSearchResultDTO>> pagedModel = pagedResourcesAssembler.toModel(page);

        return ResponseEntity.ok(pagedModel);
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
