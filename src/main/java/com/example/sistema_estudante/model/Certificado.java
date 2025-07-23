package com.example.sistema_estudante.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificados")
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O título agora pode ser opcional, já que a subcategoria já descreve a atividade.
    // Ou pode ser um complemento, ex: "Título do Artigo Publicado".
    @Column(nullable = false)
    private String titulo;

    // Esta carga horária será preenchida automaticamente com base na Subcategoria escolhida.
    @Column(nullable = false)
    private double cargaHoraria;

    @Column(columnDefinition = "TEXT")
    private String fotoBase64;

    @Column(nullable = false)
    private LocalDateTime dataEnvio;
    
    private LocalDateTime dataRevisao;

    @Column(columnDefinition = "TEXT")
    private String observacoesProfessor;
    
    // --- MUDANÇA PRINCIPAL AQUI ---
    // Removemos o campo 'tipoAtividade' e adicionamos a relação com 'subcategoria'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategoria_id", nullable = false)
    private Subcategoria subcategoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificadoStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_revisor_id")
    private Usuario professorRevisor;

    // Construtores, Getters e Setters (ATUALIZADOS)

    public Certificado() {
    }
 
    // O construtor precisa ser atualizado para refletir a mudança
    public Certificado(Long id, String titulo, double cargaHoraria, String fotoBase64,
                       LocalDateTime dataEnvio, LocalDateTime dataRevisao, String observacoesProfessor,
                       Subcategoria subcategoria, CertificadoStatus status, Usuario usuario, Usuario professorRevisor) {
        this.id = id;
        this.titulo = titulo;
        this.cargaHoraria = cargaHoraria;
        this.fotoBase64 = fotoBase64;
        this.dataEnvio = dataEnvio;
        this.dataRevisao = dataRevisao;
        this.observacoesProfessor = observacoesProfessor;
        this.subcategoria = subcategoria; // Atualizado
        this.status = status;
        this.usuario = usuario;
        this.professorRevisor = professorRevisor;
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

    public double getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(double cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public LocalDateTime getDataRevisao() {
        return dataRevisao;
    }

    public void setDataRevisao(LocalDateTime dataRevisao) {
        this.dataRevisao = dataRevisao;
    }

    public String getObservacoesProfessor() {
        return observacoesProfessor;
    }

    public void setObservacoesProfessor(String observacoesProfessor) {
        this.observacoesProfessor = observacoesProfessor;
    }

    // Getter e Setter para a nova relação
    public Subcategoria getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
    }

    public CertificadoStatus getStatus() {
        return status;
    }

    public void setStatus(CertificadoStatus status) {
        this.status = status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


    public Usuario getProfessorRevisor() {
        return professorRevisor;
    }

    public void setProfessorRevisor(Usuario professorRevisor) {
        this.professorRevisor = professorRevisor;
    }
}