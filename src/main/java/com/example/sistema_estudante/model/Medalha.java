package com.example.sistema_estudante.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "medalhas")
public class Medalha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false, length = 512)
    private String descricao;

    @Column(nullable = false)
    private String imagemUrl;

    // NOVO CAMPO: Define para qual perfil esta medalha é destinada.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfilAlvo;

    public Medalha() {}

    // Construtor atualizado para incluir o novo campo
    public Medalha(String nome, String descricao, String imagemUrl, Perfil perfilAlvo) {
        this.nome = nome;
        this.descricao = descricao;
        this.imagemUrl = imagemUrl;
        this.perfilAlvo = perfilAlvo;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
    public Perfil getPerfilAlvo() { return perfilAlvo; }
    public void setPerfilAlvo(Perfil perfilAlvo) { this.perfilAlvo = perfilAlvo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medalha medalha = (Medalha) o;
        return Objects.equals(nome, medalha.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}
