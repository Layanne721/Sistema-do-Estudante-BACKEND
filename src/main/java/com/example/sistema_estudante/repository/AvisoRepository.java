package com.example.sistema_estudante.repository;

import com.example.sistema_estudante.model.Aviso;
import com.example.sistema_estudante.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvisoRepository extends JpaRepository<Aviso, Long> {

    @Override
    @EntityGraph(attributePaths = {"autor", "lidoPorUsuarios"})
    Optional<Aviso> findById(Long id);

    @EntityGraph(attributePaths = {"autor"})
    List<Aviso> findByAutorOrderByDataPublicacaoDesc(Usuario autor);
    
    // ✅ CORREÇÃO: Adicionado "lidoPorUsuarios" ao EntityGraph
    // para garantir que a coleção seja carregada antes da consulta.
    @Query("SELECT a FROM Aviso a WHERE :usuario NOT MEMBER OF a.lidoPorUsuarios ORDER BY a.dataPublicacao DESC")
    @EntityGraph(attributePaths = {"autor", "lidoPorUsuarios"})
    List<Aviso> findAvisosNaoLidosPorUsuario(@Param("usuario") Usuario usuario);

    // ✅ CORREÇÃO: Adicionado "lidoPorUsuarios" ao EntityGraph aqui também.
    @Query("SELECT a FROM Aviso a WHERE :usuario MEMBER OF a.lidoPorUsuarios ORDER BY a.dataPublicacao DESC")
    @EntityGraph(attributePaths = {"autor", "lidoPorUsuarios"})
    List<Aviso> findAvisosLidosPorUsuario(@Param("usuario") Usuario usuario);
}