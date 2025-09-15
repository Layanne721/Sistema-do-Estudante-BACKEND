package com.example.sistema_estudante.dto;

import com.example.sistema_estudante.model.CertificadoStatus;
import java.time.LocalDateTime;

public class CertificadoDTO {
    // Campos que permanecem
    private Long id;
    private String titulo;
    private double cargaHoraria;
    private String fotoBase64;
    private String mimeType; // Campo para o tipo de arquivo
    private LocalDateTime dataEnvio;
    private LocalDateTime dataRevisao;
    private String observacoesProfessor;
    private CertificadoStatus status;
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioMatricula; // Mantido para a funcionalidade de revisão
    private Long professorRevisorId;
    private String professorRevisorNome;

    // Campo para ENVIAR dados (do frontend para o backend)
    private Long subcategoriaId; 

    // Campos para RECEBER dados (do backend para o frontend)
    private String subcategoriaDescricao;
    private String modalidadeNome;

    public CertificadoDTO() {
    }
    
    // Getters e Setters (para todos os campos)

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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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

    public CertificadoStatus getStatus() {
        return status;
    }

    public void setStatus(CertificadoStatus status) {
        this.status = status;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }
    
    public String getUsuarioMatricula() {
        return usuarioMatricula;
    }

    public void setUsuarioMatricula(String usuarioMatricula) {
        this.usuarioMatricula = usuarioMatricula;
    }

    public Long getProfessorRevisorId() {
        return professorRevisorId;
    }

    public void setProfessorRevisorId(Long professorRevisorId) {
        this.professorRevisorId = professorRevisorId;
    }

    public String getProfessorRevisorNome() {
        return professorRevisorNome;
    }

    public void setProfessorRevisorNome(String professorRevisorNome) {
        this.professorRevisorNome = professorRevisorNome;
    }

    public Long getSubcategoriaId() {
        return subcategoriaId;
    }

    public void setSubcategoriaId(Long subcategoriaId) {
        this.subcategoriaId = subcategoriaId;
    }

    public String getSubcategoriaDescricao() {
        return subcategoriaDescricao;
    }

    public void setSubcategoriaDescricao(String subcategoriaDescricao) {
        this.subcategoriaDescricao = subcategoriaDescricao;
    }

    public String getModalidadeNome() {
        return modalidadeNome;
    }

    public void setModalidadeNome(String modalidadeNome) {
        this.modalidadeNome = modalidadeNome;
    }
}

