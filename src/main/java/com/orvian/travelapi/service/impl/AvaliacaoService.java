package com.orvian.travelapi.service.impl;

import com.orvian.travelapi.controller.dto.AvaliacaoDTO;
import com.orvian.travelapi.domain.model.Avaliacao;
import com.orvian.travelapi.domain.model.User;
import com.orvian.travelapi.domain.repository.AvaliacaoRepository;
import com.orvian.travelapi.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvaliacaoService {
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    @Autowired
    private UserRepository userRepository;

    public AvaliacaoDTO criarAvaliacao(AvaliacaoDTO dto) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(dto.getNota());
        avaliacao.setComentario(dto.getComentario());
        avaliacao.setDataAvaliacao(dto.getDataAvaliacao());
        avaliacao.setTitulo(dto.getTitulo());
        avaliacao.setRecomendaria(dto.getRecomendaria());
        // Simples: buscar usuário e pacote (mock)
        User user = userRepository.findById(dto.getUser_id()).orElse(null);
        avaliacao.setUser(user);
        // TravelPackage pacote = ... buscar pacote
        avaliacaoRepository.save(avaliacao);
        dto.setId(avaliacao.getId());
        return dto;
    }

    public List<AvaliacaoDTO> listarAvaliacoes() {
        return avaliacaoRepository.findAll().stream().map(a -> {
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
        return avaliacaoRepository.findById(id).map(a -> {
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
    }
}
