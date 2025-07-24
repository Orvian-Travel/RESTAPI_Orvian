package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.reservation.CreateReservationDTO;
import com.orvian.travelapi.controller.dto.reservation.ReservationSearchResultDTO;
import com.orvian.travelapi.domain.model.Reservation;
import com.orvian.travelapi.domain.repository.ReservationRepository;
import com.orvian.travelapi.service.impl.ReservationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gerenciamento de Reservas", description = "Endpoints para gerenciamento de reservas")
public class ReservationControllerImpl implements GenericController {

    private final ReservationServiceImpl reservationService;
    private final ReservationRepository reservationRepository;

    @PostMapping
    @Operation(summary = "Criar uma nova reserva", description = "Cria uma nova reserva com os detalhes fornecidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Já existe uma reserva com os mesmos dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> create(@Valid @RequestBody CreateReservationDTO dto){
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
    public ResponseEntity<List<ReservationSearchResultDTO>> findAll() {
        log.info("Fetching all reservations");
        List<ReservationSearchResultDTO> reservations = reservationService.findAll();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID", description = "Busca uma reserva específica pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ReservationSearchResultDTO> findById(@PathVariable UUID id){
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

    @PutMapping("/cancel/{id}")
    @Operation(summary = "Cancelar reserva por ID", description = "Atualiza o status de uma reserva para cancelada pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> updateCancel(@PathVariable UUID id) {
        log.info("Updating reservation with ID: {}", id);
        reservationService.updateCancel(id);
        return ResponseEntity.ok().build();
    }
}
