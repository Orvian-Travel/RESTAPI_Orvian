package com.orvian.travelapi.service.impl;

import com.orvian.travelapi.controller.dto.traveler.CreateTravelerDTO;
import com.orvian.travelapi.controller.dto.traveler.TravelerSearchResultDTO;
import com.orvian.travelapi.controller.dto.traveler.UpdateTravelerDTO;
import com.orvian.travelapi.domain.model.Traveler;
import com.orvian.travelapi.domain.repository.TravelerRepository;
import com.orvian.travelapi.mapper.TravelerMapper;
import com.orvian.travelapi.service.TravelerService;
import com.orvian.travelapi.service.exception.DuplicatedRegistryException;
import com.orvian.travelapi.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* @Transactional serve para melhorar a performance e garantir segurança.
    por exemplo:
    Garantir que operações múltiplas sejam atômicas (tudo ou nada)
    Simplificar o gerenciamento de transações (sem código manual)
    Manter consistência dos dados (rollback automático em falhas)
    Otimizar performance (especialmente com readOnly = true)
    Controlar comportamento transacional (timeout, isolamento, propagação)

    colocar a anotação na classe faz com que todos os metodos herdem a anotação implicitamente.
 */
@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class TravelerServiceImpl implements TravelerService {

    private final TravelerRepository travelerRepository;
    private final TravelerMapper travelerMapper;

    @Override
    @Transactional(readOnly = true) // sobreesceve informando que é apenas para leitura
    public List<TravelerSearchResultDTO> findAll() {
        log.info("retrieving all travelers");
        List<Traveler> travelerList = travelerRepository.findAll();
        return travelerMapper.toTravelerSearchResultDTOList(travelerList);
    }

    @Override
    public Traveler create(Record dto) {
        Traveler traveler = travelerMapper.toEntity((CreateTravelerDTO) dto);
        log.info("creating traveler with email {}", traveler.getEmail());
        validateCreationAndUpdate(traveler);
        log.info("traveler created with id {}", traveler.getId());
        return travelerRepository.save(traveler);
    }

    @Override
    @Transactional(readOnly = true) // sobreesceve informando que é apenas para leitura
    public TravelerSearchResultDTO findById(UUID id) {
        Traveler traveler = travelerRepository.findById(id).orElseThrow(() -> new NotFoundException("Traveler with ID " + id + " not found"));
        log.info("retrieving traveler with id {}", id);
        return travelerMapper.toDto(traveler);
    }

    @Override
    public void update(UUID id, Record dto) {
        Optional<Traveler> travelerOptional = travelerRepository.findById(id);
        if (travelerOptional.isEmpty()){
            log.error("Traveler with id {} not found", id);
            throw new NotFoundException("Traveler with ID " + id + " not found");
        }

        Traveler traveler = travelerOptional.get();
        log.info("Updating Traveler with ID {}", traveler.getId());
        validateCreationAndUpdate(traveler);

        travelerMapper.updateEntityFromDto((UpdateTravelerDTO) dto, traveler);
        Traveler updatedTraveler = travelerRepository.save(traveler);
        log.info("Traveler updated with ID {}", updatedTraveler.getId());
    }

    @Override
    public void delete(UUID id) {
        log.info("deleting traveler with id {}", id);
        travelerRepository.deleteById(id);
        log.info("traveler with id {} deleted successfully", id);
    }

    public void validateCreationAndUpdate(Traveler traveler){
        if (isDuplicateTraveler(traveler)){
            log.error("Traverler already registered: {}", traveler);
            throw new DuplicatedRegistryException("Traveler already exists");
        }
    }

    public boolean isDuplicateTraveler(Traveler traveler){
        Optional<Traveler> travelerOptional = travelerRepository.findbyEmailOrCpf(traveler.getEmail(), traveler.getCpf());

        if (traveler.getId() == null){
            return travelerOptional.isPresent();
        }

        return !traveler.getId().equals(travelerOptional.get().getId()) && travelerOptional.isPresent();
    }

    public Traveler create(CreateTravelerDTO dto){
        Traveler traveler = travelerMapper.toEntity(dto);
        log.info("creating traveler with email: {}", traveler.getEmail());
        validateCreationAndUpdate(traveler);
        log.info("traveler created with id: {}", traveler.getId());
        return travelerRepository.save(traveler);
    }
}
