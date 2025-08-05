package com.orvian.travelapi.service.impl;

import com.orvian.travelapi.controller.dto.rating.CreateRatingDTO;
import com.orvian.travelapi.controller.dto.rating.RatingDTO;
import com.orvian.travelapi.domain.model.Rating;
import com.orvian.travelapi.domain.model.Reservation;
import com.orvian.travelapi.domain.model.TravelPackage;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.RatingRepository;
import com.orvian.travelapi.domain.repository.ReservationRepository;
import com.orvian.travelapi.domain.repository.UserRepository;
import com.orvian.travelapi.service.RatingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RatingDTO create(CreateRatingDTO dto, UUID userId) {
        // Validar dados de entrada
        if (dto.rate() < 1 || dto.rate() > 5) {
            throw new IllegalArgumentException("A nota deve estar entre 1 e 5");
        }

        Reservation reservation = reservationRepository.findById(dto.reservationId())
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada"));

        // Verificar se já existe uma avaliação para esta reserva
        if (ratingRepository.existsByReservationId(dto.reservationId())) {
            throw new IllegalStateException("Esta reserva já foi avaliada");
        }

        // Verificar se a reserva pertence ao usuário autenticado
        if (!reservation.getUser().getId().equals(userId)) {
            throw new SecurityException("Você não tem permissão para avaliar esta reserva");
        }

        Rating rating = new Rating();
        rating.setRate(dto.rate());
        rating.setComment(dto.comment());
        rating.setReservation(reservation);

        Rating savedRating = ratingRepository.save(rating);
        return toDTO(savedRating);
    }

    @Override
    public List<RatingDTO> findByTravelPackage(UUID travelPackageId) {
        return ratingRepository.findByTravelPackageIdOrderByCreatedAtDesc(travelPackageId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatingDTO> findAll() {
        return ratingRepository.findAllWithDetails().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RatingDTO findById(UUID id) {
        return ratingRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada"));
    }

    @Override
    public void delete(UUID id) {
        ratingRepository.deleteById(id);
    }

    private RatingDTO toDTO(Rating rating) {
        // Verificações defensivas
        if (rating.getReservation() == null) {
            throw new IllegalStateException("Rating com ID " + rating.getId() + " não possui reserva associada");
        }

        if (rating.getReservation().getUser() == null) {
            throw new IllegalStateException("Reserva não possui usuário associado");
        }

        if (rating.getReservation().getPackageDate() == null ||
                rating.getReservation().getPackageDate().getTravelPackage() == null) {
            throw new IllegalStateException("Reserva não possui pacote associado");
        }

        User user = rating.getReservation().getUser();
        TravelPackage travelPackage = rating.getReservation().getPackageDate().getTravelPackage();

        return new RatingDTO(
                rating.getId(),
                rating.getRate(),
                rating.getComment(),
                rating.getCreatedAt(),
                rating.getUpdatedAt(),
                rating.getReservation().getId(),
                user.getId(),
                user.getName(),
                travelPackage.getId(),
                travelPackage.getTitle()
        );
    }
}
    // TODO - Arrumar Ratings
    //private final RatingRepository ratingRepository;
    //private final UserRepository userRepository;

    /*public AvaliacaoDTO criarAvaliacao(AvaliacaoDTO dto) {
        Rating rating = new Rating();
        rating.setNota(dto.getNota());
        rating.setComentario(dto.getComentario());
        rating.setDataAvaliacao(dto.getDataAvaliacao());
        rating.setTitulo(dto.getTitulo());
        rating.setRecomendaria(dto.getRecomendaria());
        // Simples: buscar usuário e pacote (mock)
        User user = userRepository.findById(dto.getUser_id()).orElse(null);
        rating.setUser(user);
        // TravelPackage pacote = ... buscar pacote
        ratingRepository.save(rating);
        dto.setId(rating.getId());
        return dto;
    }

    public List<AvaliacaoDTO> listarAvaliacoes() {
        return ratingRepository.findAll().stream().map(a -> {
            AvaliacaoDTO dto = new AvaliacaoDTO();
            dto.setId(a.getId());
            dto.setNota(a.getNota());
            dto.setComentario(a.getComentario());
            dto.setDataAvaliacao(a.getDataAvaliacao());
            dto.setTitulo(a.getTitulo());
            dto.setRecomendaria(a.getRecomendaria());
            if (a.getUser() != null) dto.setUserid(a.getUser().getId());
            // if (a.getPacoteViagem() != null) dto.setPacoteId(a.getPacoteViagem().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    public Optional<AvaliacaoDTO> buscarPorId(Long id) {
        return ratingRepository.findById(id).map(a -> {
            AvaliacaoDTO dto = new AvaliacaoDTO();
            dto.setId(a.getId());
            dto.setNota(a.getNota());
            dto.setComentario(a.getComentario());
            dto.setDataAvaliacao(a.getDataAvaliacao());
            dto.setTitulo(a.getTitulo());
            dto.setRecomendaria(a.getRecomendaria());
            if (a.getUser() != null) dto.setUserid(a.getUser().getId());
            // if (a.getPacoteViagem() != null) dto.setPacoteId(a.getPacoteViagem().getId());
            return dto;
        });
    }*/

