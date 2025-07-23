package com.example.sistema_estudante.dto;

import java.time.LocalDateTime; 

public class AvisoDTO {
    private Long id;
    private String titulo;
    private LocalDateTime dataPublicacao;
    private AutorDTO autor;

    // Construtores
    public AvisoDTO() {
    }

    public AvisoDTO(Long id, String titulo, LocalDateTime dataPublicacao, AutorDTO autor) {
        this.id = id;
        this.titulo = titulo;
        this.dataPublicacao = dataPublicacao;
        this.autor = autor;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public AutorDTO getAutor() {
        return autor;
    }

    public void setAutor(AutorDTO autor) {
        this.autor = autor;
    }
}