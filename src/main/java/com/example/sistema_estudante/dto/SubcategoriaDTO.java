package com.example.sistema_estudante.dto;

public class SubcategoriaDTO {
    private Long id;
    private String descricao;
    private int horas;

    public SubcategoriaDTO() {
    }

    public SubcategoriaDTO(Long id, String descricao, int horas) {
        this.id = id;
        this.descricao = descricao;
        this.horas = horas;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }
}