package com.example.sistema_estudante.dto;

import com.example.sistema_estudante.model.StatusAviso;
import com.example.sistema_estudante.model.ModalidadeTipo;
import com.example.sistema_estudante.model.RelevanciaAviso;

public class AvisoRequestDTO {
    private String titulo;
    private String conteudo;
    private ModalidadeTipo tipoAtividade;
    private StatusAviso status;
    private RelevanciaAviso relevancia;

    // Getters e Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public ModalidadeTipo getTipoAtividade() { return tipoAtividade; }
    public void setTipoAtividade(ModalidadeTipo tipoAtividade) { this.tipoAtividade = tipoAtividade; }
    public StatusAviso getStatus() { return status; }
    public void setStatus(StatusAviso status) { this.status = status; }
    public RelevanciaAviso getRelevancia() { return relevancia; }
    public void setRelevancia(RelevanciaAviso relevancia) { this.relevancia = relevancia; }
}