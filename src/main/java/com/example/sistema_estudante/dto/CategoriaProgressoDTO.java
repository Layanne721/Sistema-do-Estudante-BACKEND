// Local: src/main/java/com/example/sistema_estudante/dto/CategoriaProgressoDTO.java
package com.example.sistema_estudante.dto;

/**
 * DTO que detalha o progresso de uma categoria específica.
 * @param horasValidadas As horas do aluno nessa categoria (já com o teto aplicado).
 * @param horasBrutas As horas totais que o aluno acumulou na categoria (sem teto).
 * @param maxHoras O limite máximo de horas para esta categoria.
 */
public record CategoriaProgressoDTO(
    double horasValidadas,
    double horasBrutas,
    int maxHoras
) {}