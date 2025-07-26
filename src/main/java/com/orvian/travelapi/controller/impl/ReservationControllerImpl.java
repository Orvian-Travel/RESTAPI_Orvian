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
import com.orvian.travelapi.controller.dto.reservation.CreateReservationDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationSearchResultDTO;
import com.orvian.travelapi.domain.model.Reservation;
import com.orvian.travelapi.service.impl.ReservationServiceImpl;

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
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gerenciamento de Reservas", description = "Endpoints para gerenciamento de reservas")
public class ReservationControllerImpl implements GenericController {

    private final ReservationServiceImpl reservationService;

    private final PagedResourcesAssembler<ReservationSearchResultDTO> pagedResourcesAssembler;

    @PostMapping
    @Operation(summary = "Criar uma nova reserva", description = "Cria uma nova reserva com os detalhes fornecidos.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Já existe uma reserva com os mesmos dados"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> create(@Valid @RequestBody CreateReservationDTO dto) {
        Reservation reservation = reservationService.create(dto);
        URI location = generateHeaderLocation(reservation.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @Operation(summary = "Listar todas as reservas", description = "Busca uma lista de todas as reservas disponíveis.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservas recuperadas com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PagedModel<EntityModel<ReservationSearchResultDTO>>> getReservationsByPage(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) UUID userId) {

        Page<ReservationSearchResultDTO> page = reservationService.findAll(pageNumber, pageSize, userId);
        PagedModel<EntityModel<ReservationSearchResultDTO>> pagedModel = pagedResourcesAssembler.toModel(page);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID", description = "Busca uma reserva específica pelo seu ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva encontrada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Reserva não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ReservationSearchResultDTO> findById(@PathVariable UUID id) {
        ReservationSearchResultDTO reservation = reservationService.findById(id);
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar reserva por ID", description = "Remove uma reserva específica pelo seu ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reserva deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Reserva não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("Deleting reservation with ID: {}", id);
        reservationService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar reserva por ID", description = "Atualiza o status de uma reserva para cancelada pelo seu ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reserva atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "404", description = "Reserva não encontrada", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ResponseErrorDTO.class)))
    })
    public ResponseEntity<Void> updateCancel(@PathVariable UUID id, @RequestBody @Valid CreateReservationDTO dto) {
        log.info("Updating reservation with ID: {}", id);
        reservationService.update(id, dto);
        return ResponseEntity.ok().build();
    }
}
