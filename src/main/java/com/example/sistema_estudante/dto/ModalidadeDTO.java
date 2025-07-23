package com.example.sistema_estudante.dto;

public class ModalidadeDTO {
    private Long id;
    private String nome;
    private int maxHoras;

    public ModalidadeDTO() {
    }

    public ModalidadeDTO(Long id, String nome, int maxHoras) {
        this.id = id;
        this.nome = nome;
        this.maxHoras = maxHoras;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public int getMaxHoras() {
        return maxHoras;
    }

    public void setMaxHoras(int maxHoras) {
        this.maxHoras = maxHoras;
    }
}