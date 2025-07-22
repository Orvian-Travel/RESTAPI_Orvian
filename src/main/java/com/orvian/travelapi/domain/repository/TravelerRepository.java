package com.orvian.travelapi.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orvian.travelapi.domain.model.Traveler;

public interface TravelerRepository extends JpaRepository<Traveler, UUID> {
    // metodo para buscar todos os viajantes de uma reserva
    //Optional<List<Traveler>> findByReservation(Reservation reservation);

    Optional<Traveler> findByEmailOrCpf(String email, String cpf);
}
