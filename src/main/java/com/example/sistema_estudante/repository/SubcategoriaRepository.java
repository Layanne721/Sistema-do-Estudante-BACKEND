package com.example.sistema_estudante.repository;

import com.example.sistema_estudante.model.Subcategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Long> {
    
    /**
     * Busca todas as subcategorias pertencentes a um ID de modalidade específico.
     * Será usado para popular o dropdown de subcategorias no frontend.
     * @param modalidadeId O ID da modalidade pai.
     * @return Uma lista de subcategorias.
     */
    List<Subcategoria> findByModalidadeId(Long modalidadeId);
}