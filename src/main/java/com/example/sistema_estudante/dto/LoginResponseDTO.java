package com.example.sistema_estudante.dto;

import com.example.sistema_estudante.model.ModalidadeTipo;
import com.example.sistema_estudante.model.Perfil;

public record LoginResponseDTO(
    String token,
    String nome,
    Perfil perfil,
    ModalidadeTipo tipoAtividadeGerenciada,
    String avatarUrl
) {}