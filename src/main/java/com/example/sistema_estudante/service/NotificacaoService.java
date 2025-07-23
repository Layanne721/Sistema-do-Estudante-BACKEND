package com.example.sistema_estudante.service;

import com.example.sistema_estudante.model.Notificacao;
import com.example.sistema_estudante.model.Usuario;
import com.example.sistema_estudante.repository.NotificacaoRepository;
import com.example.sistema_estudante.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository, UsuarioRepository usuarioRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Notificacao criarNotificacao(Usuario destinatario, String mensagem, String cor, String link) {
        // CORREÇÃO: Usando o construtor manual de Notificacao
        Notificacao notificacao = new Notificacao(
            null, // ID (será gerado automaticamente pelo DB)
            destinatario,
            mensagem,
            link,
            LocalDateTime.now(),
            false, // lido = false por padrão
            cor
        );
        return notificacaoRepository.save(notificacao);
    }

    @Transactional(readOnly = true)
    public List<Notificacao> listarNotificacoesDoUsuario(Usuario usuario) {
        return notificacaoRepository.findByUsuarioOrderByDataEnvioDesc(usuario);
    }

    @Transactional(readOnly = true)
    public List<Notificacao> listarNotificacoesNaoLidasDoUsuario(Usuario usuario) {
        return notificacaoRepository.findByUsuarioAndLidoFalseOrderByDataEnvioDesc(usuario);
    }

    @Transactional
    public Notificacao marcarNotificacaoComoLida(Long notificacaoId, String userEmail) {
        Usuario usuarioAutenticado = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userEmail));

        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada com ID: " + notificacaoId));
        
        if (!notificacao.getUsuario().getId().equals(usuarioAutenticado.getId())) {
            throw new SecurityException("Usuário não autorizado a marcar esta notificação como lida.");
        }
        
        notificacao.setLido(true);
        return notificacaoRepository.save(notificacao);
    }
}