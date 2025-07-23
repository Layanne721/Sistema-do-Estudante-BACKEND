// Local: com/example/sistema_estudante/model/Aviso.java
package com.example.sistema_estudante.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "avisos")
public class Aviso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String conteudo;
    private LocalDateTime dataPublicacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Enumerated(EnumType.STRING)
    private ModalidadeTipo tipoAtividade;
    
    @Enumerated(EnumType.STRING)
    private StatusAviso status;

    @Enumerated(EnumType.STRING)
    private RelevanciaAviso relevancia;

    // ✅ Este "mappedBy" já estava correto.
    @ManyToMany(mappedBy = "avisosLidos")
    @JsonIgnore
    private Set<Usuario> lidoPorUsuarios = new HashSet<>();

    // Getters e Setters ...
    public Aviso() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public LocalDateTime getDataPublicacao() { return dataPublicacao; }
    public void setDataPublicacao(LocalDateTime dataPublicacao) { this.dataPublicacao = dataPublicacao; }
    public Usuario getAutor() { return autor; } 
    public void setAutor(Usuario autor) { this.autor = autor; }
    public ModalidadeTipo getTipoAtividade() { return tipoAtividade; }
    public void setTipoAtividade(ModalidadeTipo tipoAtividade) { this.tipoAtividade = tipoAtividade; }
    public StatusAviso getStatus() { return status; }
    public void setStatus(StatusAviso status) { this.status = status; }
    public RelevanciaAviso getRelevancia() { return relevancia; }
    public void setRelevancia(RelevanciaAviso relevancia) { this.relevancia = relevancia; }
    public Set<Usuario> getLidoPorUsuarios() { return lidoPorUsuarios; }
    public void setLidoPorUsuarios(Set<Usuario> lidoPorUsuarios) { this.lidoPorUsuarios = lidoPorUsuarios; }
}