package com.example.sistema_estudante.dto;

import com.example.sistema_estudante.model.StatusAviso;
import com.example.sistema_estudante.model.ModalidadeTipo;
import com.example.sistema_estudante.model.RelevanciaAviso;


import java.time.LocalDateTime;


public class AvisoResponseDTO {
    private Long id;
    private String titulo;
    private String conteudo;
    private LocalDateTime dataPublicacao;
    private String autorNome;
    private ModalidadeTipo tipoAtividade;
    private StatusAviso status;
    private RelevanciaAviso relevancia;
    private boolean podeEditarOuExcluir;
    private boolean lidoPeloUsuarioAtual;


    public AvisoResponseDTO() {
    }

    public AvisoResponseDTO(Long id, String titulo, String conteudo, LocalDateTime dataPublicacao, String autorNome,
                             ModalidadeTipo tipoAtividade, StatusAviso status, RelevanciaAviso relevancia,
                             boolean podeEditarOuExcluir, boolean lidoPeloUsuarioAtual) {
        this.id = id;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.dataPublicacao = dataPublicacao;
        this.autorNome = autorNome;
        this.tipoAtividade = tipoAtividade;
        this.status = status;
        this.relevancia = relevancia;
        this.podeEditarOuExcluir = podeEditarOuExcluir;
        this.lidoPeloUsuarioAtual = lidoPeloUsuarioAtual;
    }

    // Getters e Setters manuais
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

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public String getAutorNome() {
        return autorNome;
    }

    public void setAutorNome(String autorNome) {
        this.autorNome = autorNome;
    }

    public ModalidadeTipo getTipoAtividade() {
        return tipoAtividade;
    }

    public void setTipoAtividade(ModalidadeTipo tipoAtividade) {
        this.tipoAtividade = tipoAtividade;
    }

    public StatusAviso getStatus() {
        return status;
    }

    public void setStatus(StatusAviso status) {
        this.status = status;
    }

    public RelevanciaAviso getRelevancia() {
        return relevancia;
    }

    public void setRelevancia(RelevanciaAviso relevancia) {
        this.relevancia = relevancia;
    }

    public boolean isPodeEditarOuExcluir() {
        return podeEditarOuExcluir;
    }

    public void setPodeEditarOuExcluir(boolean podeEditarOuExcluir) {
        this.podeEditarOuExcluir = podeEditarOuExcluir;
    }

    public boolean isLidoPeloUsuarioAtual() {
        return lidoPeloUsuarioAtual;
    }

    public void setLidoPeloUsuarioAtual(boolean lidoPeloUsuarioAtual) {
        this.lidoPeloUsuarioAtual = lidoPeloUsuarioAtual;
    }
}