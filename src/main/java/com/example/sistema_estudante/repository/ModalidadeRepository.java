package com.example.sistema_estudante.repository;

import com.example.sistema_estudante.model.Modalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModalidadeRepository extends JpaRepository<Modalidade, Long> {
    // Métodos básicos de CRUD (save, findById, findAll, delete) já estão inclusos.
    // Nenhum método customizado é necessário por enquanto.
}