package com.orvian.travelapi.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

public class AvaliacaoDTO {
    // Getters and setters
    @Setter
    @Getter
    private Long id;
    private Long Package_id;
    private UUID User_id;
    private Integer nota;
    private String comentario;
    private LocalDate dataAvaliacao;
    private String titulo;
    private Boolean recomendaria;

    public Long getPackageid() { return Package_id; }
    public void setPackage_id(Long pacoteId) { this.Package_id = pacoteId; }
    public UUID getUser_id() { return User_id; }
    public void setUserid(UUID User_id) { this.User_id = User_id; }
    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDate getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDate dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Boolean getRecomendaria() { return recomendaria; }
    public void setRecomendaria(Boolean recomendaria) { this.recomendaria = recomendaria; }
}
