package com.example.sistema_estudante.dto;

import com.example.sistema_estudante.model.CertificadoStatus;

public record CertificadoRevisaoDTO(
    CertificadoStatus status,
    String observacoesProfessor
) {}