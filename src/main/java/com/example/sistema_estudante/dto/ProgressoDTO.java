// Local: src/main/java/com/example/sistema_estudante/dto/ProgressoDTO.java
package com.example.sistema_estudante.dto;

import java.util.Map;

/**
 * DTO principal que encapsula todo o progresso do aluno.
 * @param totalHorasValidadas O total de horas válidas após aplicar todos os tetos (da categoria e o geral de 200).
 * @param totalHorasBrutas O total de horas que o aluno enviou e foram aprovadas, sem nenhum teto.
 * @param metaHoras A meta total de horas a ser atingida (ex: 200).
 * @param progressoPorCategoria Um mapa detalhando o progresso em cada categoria.
 */
public record ProgressoDTO(
    double totalHorasValidadas,
    double totalHorasBrutas,
    int metaHoras,
    Map<String, CategoriaProgressoDTO> progressoPorCategoria
) {}