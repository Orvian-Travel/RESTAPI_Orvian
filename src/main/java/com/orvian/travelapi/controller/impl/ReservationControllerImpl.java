package com.orvian.travelapi.controller.impl;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.reservation.CreateReservationDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationDateDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationSearchResultDTO;
import com.orvian.travelapi.domain.enums.ReservationSituation;
import com.orvian.travelapi.domain.model.Reservation;
import com.orvian.travelapi.service.ReservationService;
import com.orvian.travelapi.service.exception.AccessDeniedException;
import com.orvian.travelapi.service.security.OrvianAuthorizationService;

import io.swagger.v3.oas.annotations.Operation;
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

    private final ReservationService reservationService;

    private final PagedResourcesAssembler<ReservationSearchResultDTO> pagedResourcesAssembler;

    private final OrvianAuthorizationService authorizationService;

    @PostMapping
    @Operation(summary = "Criar uma nova reserva", description = "Cria uma nova reserva com os detalhes fornecidos.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Já existe uma reserva com os mesmos dados"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> create(@Valid @RequestBody CreateReservationDTO dto) {
        if (!authorizationService.canCreateResourceForUser(dto.userId(), "reservation")) {
            throw new AccessDeniedException("Você só pode criar reservas para si mesmo. Para criar reservas para outros usuários, contate um administrador.");
        }

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

        log.info("DEBUG: Received parameters - pageNumber: {}, pageSize: {}, userId: {}",
                pageNumber, pageSize, userId);

        UUID effectiveUserId = authorizationService.getEffectiveUserIdForListing(userId);

        log.info("Fetching reservations for userId: {}", effectiveUserId);
        Page<ReservationSearchResultDTO> page = reservationService.findAll(pageNumber, pageSize, effectiveUserId);

        // ✅ DEBUG: Log dos resultados
        log.info("DEBUG: Found {} reservations, total pages: {}", page.getTotalElements(), page.getTotalPages());

        PagedModel<EntityModel<ReservationSearchResultDTO>> pagedModel = pagedResourcesAssembler.toModel(page);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar reservas com filtros", description = "Busca reservas com filtros de usuário e status.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservas recuperadas com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PagedModel<EntityModel<ReservationSearchResultDTO>>> getReservationsBySearch(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) ReservationSituation status,
            @RequestParam(required = false)
            @Schema(description = "Data da reserva no formato YYYY-MM-DD", example = "2025-01-15") LocalDate reservationDate) {

        log.info("DEBUG: Search parameters - pageNumber: {}, pageSize: {}, userId: {}, status: {}",
                pageNumber, pageSize, userId, status);

        UUID effectiveUserId = authorizationService.getEffectiveUserIdForListing(userId);

        log.info("Fetching reservations for userId: {} with status filter: {} and reservationDate: {}", effectiveUserId, status, reservationDate);

        Page<ReservationSearchResultDTO> page = reservationService.findAllByStatusAndDate(
                pageNumber, pageSize, effectiveUserId, status, reservationDate);

        log.info("DEBUG: Found {} reservations, total pages: {}", page.getTotalElements(), page.getTotalPages());

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
        if (!authorizationService.canAccessReservation(id)) {
            throw new AccessDeniedException("Você só pode visualizar suas próprias reservas");
        }

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
        if (!authorizationService.canCancelReservation(id)) {
            throw new AccessDeniedException("Você só pode cancelar suas próprias reservas");
        }

        log.info("Cancelling reservation with ID: {}", id);
        reservationService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available-dates")
    @Operation(summary = "Buscar datas de reserva disponíveis",
            description = "Retorna todas as datas em que o usuário possui reservas para popular filtros de data")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datas disponíveis recuperadas com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<ReservationDateDTO>> getAvailableReservationDates(
            @RequestParam(required = false) UUID userId) {

        log.info("Fetching available reservation dates for userId: {}", userId);

        UUID effectiveUserId = authorizationService.getEffectiveUserIdForListing(userId);

        List<ReservationDateDTO> availableDates = reservationService.findAvailableReservationDates(effectiveUserId);

        log.info("Found {} available reservation dates", availableDates.size());
        return ResponseEntity.ok(availableDates);
    }

}
