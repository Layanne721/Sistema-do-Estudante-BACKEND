package com.example.sistema_estudante.dto;


public class CertificadoIndividualDTO {
 private String titulo;
 private double cargaHoraria;
 private String fotoBase64; 

 // Getters e Setters
 public String getTitulo() { return titulo; }
 public void setTitulo(String titulo) { this.titulo = titulo; }
 public double getCargaHoraria() { return cargaHoraria; }
 public void setCargaHoraria(double cargaHoraria) { this.cargaHoraria = cargaHoraria; }
 public String getFotoBase64() { return fotoBase64; }
 public void setFotoBase64(String fotoBase64) { this.fotoBase64 = fotoBase64; }
}