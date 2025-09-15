package com.example.sistema_estudante.dto;

import java.util.Set;

public record PerfilDTO(
    String nome,
    String email,
    String avatarUrl,
    int nivel,
    int xp,
    int metaXp,
    Set<MedalhaDTO> medalhas
) {}

