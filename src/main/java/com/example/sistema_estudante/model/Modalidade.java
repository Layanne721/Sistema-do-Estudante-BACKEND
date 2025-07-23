package com.example.sistema_estudante.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "modalidades")
public class Modalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome; // Ex: "Atividades de Pesquisa"

    @Column(nullable = false)
    private int maxHoras; // Ex: 100

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ModalidadeTipo tipo; // Ex: PESQUISA

    @OneToMany(mappedBy = "modalidade", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subcategoria> subcategorias;

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

    public ModalidadeTipo getTipo() {
        return tipo;
    }

    public void setTipo(ModalidadeTipo tipo) {
        this.tipo = tipo;
    }

    public List<Subcategoria> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<Subcategoria> subcategorias) {
        this.subcategorias = subcategorias;
    }
}