package com.orvian.travelapi.controller.impl;

import com.orvian.travelapi.controller.GenericController;
import com.orvian.travelapi.controller.dto.traveler.CreateTravelerDTO;
import com.orvian.travelapi.controller.dto.traveler.TravelerSearchResultDTO;
import com.orvian.travelapi.controller.dto.traveler.UpdateTravelerDTO;
import com.orvian.travelapi.domain.model.Traveler;
import com.orvian.travelapi.mapper.TravelerMapper;
import com.orvian.travelapi.service.impl.TravelerServiceImpl;
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
public class TravelerControllerImpl implements GenericController {

    private final TravelerServiceImpl travelerService;
    private final TravelerMapper travelerMapper;

    @PostMapping
    public ResponseEntity<Void> createTraveler(@Valid @RequestBody CreateTravelerDTO dto){
        log.info("Creating traveler with details: {}", dto);
        Traveler createdTraveler = travelerService.create(dto);
        log.info("Traveler created with ID {}", createdTraveler.getId());
        URI location = generateHeaderLocation(createdTraveler.getId());
        return  ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<TravelerSearchResultDTO>> getAllTravelers(){
        log.info("Fetching all travelers");
        List<TravelerSearchResultDTO> travelers = travelerService.findAll();
        log.info("total travelers found: {}", travelers.size());
        return ResponseEntity.ok(travelers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelerSearchResultDTO> getTravelerById(@PathVariable UUID id){
        return ResponseEntity.ok(travelerService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTraveler(@PathVariable UUID id, @Valid @RequestBody UpdateTravelerDTO dto){
        log.info("updating traveler with ID: {}", id);
        travelerService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraveler(@PathVariable UUID id){
        log.info("Deleting traveler with ID: {}", id);
        travelerService.delete(id);
        log.info("Traveler deleted successfully");
        return ResponseEntity.noContent().build();
    }

    //todo metodo para buscar por ID da reserva como opção extra
}
