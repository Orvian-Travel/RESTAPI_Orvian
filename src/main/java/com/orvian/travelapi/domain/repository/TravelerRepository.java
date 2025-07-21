package com.orvian.travelapi.domain.repository;

import com.orvian.travelapi.domain.model.Reservation;
import com.orvian.travelapi.domain.model.Traveler;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TravelerRepository extends JpaRepository<Traveler, UUID> {
    // metodo para buscar todos os viajantes de uma reserva
    //Optional<List<Traveler>> findByReservation(Reservation reservation);

    Optional<Traveler> findByEmailOrCpf(String email, String cpf);
}
