package com.example.sistema_estudante.dto;
import java.util.List;

public class LotePorSubcategoriaDTO {
    private Long subcategoriaId;
    private List<CertificadoIndividualDTO> certificados;

    // Getters e Setters
    public Long getSubcategoriaId() { return subcategoriaId; }
    public void setSubcategoriaId(Long subcategoriaId) { this.subcategoriaId = subcategoriaId; }
    public List<CertificadoIndividualDTO> getCertificados() { return certificados; }
    public void setCertificados(List<CertificadoIndividualDTO> certificados) { this.certificados = certificados; }
}