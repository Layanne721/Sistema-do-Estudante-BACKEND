package com.example.sistema_estudante.repository;

import com.example.sistema_estudante.model.Certificado;
import com.example.sistema_estudante.model.CertificadoStatus;
import com.example.sistema_estudante.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Long> {

    @EntityGraph(attributePaths = {"subcategoria", "subcategoria.modalidade", "usuario"})
    List<Certificado> findByUsuario(Usuario usuario);

    // ✅ CORREÇÃO APLICADA AQUI: Adicionado o EntityGraph que faltava.
    @EntityGraph(attributePaths = {"subcategoria", "subcategoria.modalidade"})
    List<Certificado> findByUsuarioAndStatus(Usuario usuario, CertificadoStatus status);
    
    @EntityGraph(attributePaths = {"subcategoria", "subcategoria.modalidade", "usuario"})
    List<Certificado> findByStatusInOrderByDataEnvioDesc(List<CertificadoStatus> statuses);

    long countByProfessorRevisorAndStatusIn(Usuario professor, List<CertificadoStatus> statuses);
}