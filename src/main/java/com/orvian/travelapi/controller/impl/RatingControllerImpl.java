package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.dto.rating.CreateRatingDTO;
import com.orvian.travelapi.controller.dto.rating.RatingDTO;
import com.orvian.travelapi.service.RatingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingControllerImpl {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingDTO> criarAvaliacao(
            @RequestBody @Valid CreateRatingDTO dto,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        try {
            return ResponseEntity.ok(ratingService.create(dto, userId));
        } catch (EntityNotFoundException e) {
            System.out.println("Erro: Entidade não encontrada - " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            // Exceção específica para reserva já avaliada
            System.out.println("Erro: Reserva já avaliada - " + e.getMessage());
            return ResponseEntity.status(409).build(); // Conflict
        } catch (SecurityException e) {
            System.out.println("Erro: Sem permissão - " + e.getMessage());
            return ResponseEntity.status(403).build(); // Forbidden
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: Dados inválidos - " + e.getMessage());
            return ResponseEntity.badRequest().build(); // Bad Request - dados inválidos
        } catch (Exception e) {
            System.out.println("Erro interno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build(); // Internal Server Error
        }
    }

    @GetMapping
    public ResponseEntity<List<RatingDTO>> listarAvaliacoes() {
        System.out.println("DEBUG: Endpoint /api/v1/ratings chamado");
        try {
            List<RatingDTO> ratings = ratingService.findAll();
            System.out.println("DEBUG: Encontradas " + ratings.size() + " avaliações");
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            System.out.println("DEBUG: Erro ao buscar avaliações: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/package/{packageId}")
    public ResponseEntity<List<RatingDTO>> listarAvaliacoesPorPacote(@PathVariable UUID packageId) {
        return ResponseEntity.ok(ratingService.findByTravelPackage(packageId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(ratingService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAvaliacao(@PathVariable UUID id) {
        try {
            ratingService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            System.out.println("Erro: Avaliação não encontrada - " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
