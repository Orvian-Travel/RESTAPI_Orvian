package com.orvian.travelapi.service.impl;

import com.orvian.travelapi.domain.repository.RatingRepository;
import com.orvian.travelapi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl {

    // TODO - Arrumar Ratings
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

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
}
