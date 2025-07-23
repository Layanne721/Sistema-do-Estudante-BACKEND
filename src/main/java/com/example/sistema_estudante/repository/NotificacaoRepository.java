package com.example.sistema_estudante.repository;

import com.example.sistema_estudante.model.Notificacao;
import com.example.sistema_estudante.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuarioOrderByDataEnvioDesc(Usuario usuario);
    List<Notificacao> findByUsuarioAndLidoFalseOrderByDataEnvioDesc(Usuario usuario);
}