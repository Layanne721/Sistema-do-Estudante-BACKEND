package com.example.sistema_estudante.repository;

import com.example.sistema_estudante.model.Medalha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedalhaRepository extends JpaRepository<Medalha, Long> {
    
    Optional<Medalha> findByNome(String nome);
}