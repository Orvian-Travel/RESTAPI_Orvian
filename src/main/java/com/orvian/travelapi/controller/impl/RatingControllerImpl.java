package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.service.impl.RatingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingControllerImpl {

    private final RatingServiceImpl ratingService;

    /*@PostMapping
    public ResponseEntity<AvaliacaoDTO> criarAvaliacao(@RequestBody AvaliacaoDTO dto) {
        AvaliacaoDTO criada = ratingService.criarAvaliacao(dto);
        return ResponseEntity.ok(criada);
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoDTO>> listarAvaliacoes() {
        return ResponseEntity.ok(ratingService.listarAvaliacoes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoDTO> buscarPorId(@PathVariable Long id) {
        return ratingService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }*/
}
