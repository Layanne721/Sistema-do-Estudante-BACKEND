package com.example.sistema_estudante.dto;

import com.example.sistema_estudante.model.Perfil;
import com.example.sistema_estudante.model.ModalidadeTipo;

// ✅ NOMES DOS CAMPOS CORRIGIDOS PARA BATER COM O FRONTEND
public class LoginResponseDTO {
    private String token;
    private String nome;
    private Perfil perfil;
    private ModalidadeTipo tipoAtividadeGerenciada;

    public LoginResponseDTO(String token, String nome, Perfil perfil, ModalidadeTipo tipoAtividadeGerenciada) {
        this.token = token;
        this.nome = nome;
        this.perfil = perfil;
        this.tipoAtividadeGerenciada = tipoAtividadeGerenciada;
    }

    // Getters e Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
    public ModalidadeTipo getTipoAtividadeGerenciada() { return tipoAtividadeGerenciada; }
    public void setTipoAtividadeGerenciada(ModalidadeTipo tipoAtividadeGerenciada) { this.tipoAtividadeGerenciada = tipoAtividadeGerenciada; }
}