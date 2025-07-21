package com.orvian.travelapi.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
public class Avaliacao {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Package_id")
    private TravelPackage travelPackage;

    @ManyToOne
    @JoinColumn(name = "User_id")
    private User user;

    private Integer nota; // de 1 a 5
    private String comentario;
    private LocalDate dataAvaliacao;

    // Outros campos opcionais
    private String titulo;
    private Boolean recomendaria; // true/false se recomendaria o pacote

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getRecomendaria() {
        return recomendaria;
    }

    public void setRecomendaria(Boolean recomendaria) {
        this.recomendaria = recomendaria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public TravelPackage getTravelPackage() {
        return travelPackage;
    }

    public void setTravelPackage(TravelPackage travelPackage) {
        this.travelPackage = travelPackage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDate getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDate dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }
}
