package com.example.sistema_estudante.service;

import com.example.sistema_estudante.dto.AvisoRequestDTO;
import com.example.sistema_estudante.dto.AvisoResponseDTO;
import com.example.sistema_estudante.model.Aviso;
import com.example.sistema_estudante.model.Perfil;
import com.example.sistema_estudante.model.Usuario;
import com.example.sistema_estudante.repository.AvisoRepository;
import com.example.sistema_estudante.repository.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvisoService {

    private final AvisoRepository avisoRepository;
    private final UsuarioRepository usuarioRepository;

    public AvisoService(AvisoRepository a, UsuarioRepository u) { this.avisoRepository = a; this.usuarioRepository = u; }

    private Usuario getUsuario(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<AvisoResponseDTO> buscarNaoLidos(String userEmail) {
        Usuario usuarioAtual = getUsuario(userEmail);
        List<Aviso> avisos = (usuarioAtual.getPerfil() == Perfil.ALUNO)
            ? avisoRepository.findAvisosNaoLidosPorUsuario(usuarioAtual)
            : avisoRepository.findByAutorOrderByDataPublicacaoDesc(usuarioAtual);
        return avisos.stream().map(aviso -> mapToResponseDTO(aviso, usuarioAtual)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AvisoResponseDTO> buscarHistorico(String userEmail) {
        Usuario usuarioAtual = getUsuario(userEmail);
        List<Aviso> avisos = (usuarioAtual.getPerfil() == Perfil.ALUNO)
            ? avisoRepository.findAvisosLidosPorUsuario(usuarioAtual)
            : avisoRepository.findByAutorOrderByDataPublicacaoDesc(usuarioAtual);
        return avisos.stream().map(aviso -> mapToResponseDTO(aviso, usuarioAtual)).collect(Collectors.toList());
    }

    @Transactional
    public void marcarComoLido(Long avisoId, String userEmail) {
        Usuario usuario = getUsuario(userEmail);
        if (usuario.getPerfil() != Perfil.ALUNO) {
            throw new AccessDeniedException("Apenas alunos podem marcar avisos como lidos.");
        }
        Aviso aviso = avisoRepository.findById(avisoId).orElseThrow(() -> new IllegalArgumentException("Aviso não encontrado"));
        usuario.getAvisosLidos().add(aviso);
        aviso.getLidoPorUsuarios().add(usuario);
        usuarioRepository.save(usuario);
    }
    
    @Transactional
    public AvisoResponseDTO criarAviso(AvisoRequestDTO dto, String userEmail) {
        Usuario autor = getUsuario(userEmail);
        if (autor.getPerfil() != Perfil.PROFESSOR) {
            throw new AccessDeniedException("Apenas professores podem criar avisos.");
        }
        if (autor.getTipoAtividadeGerenciada() == null) {
            throw new IllegalStateException("Professor não tem modalidade de atuação definida.");
        }
        Aviso aviso = new Aviso();
        aviso.setTitulo(dto.getTitulo());
        aviso.setConteudo(dto.getConteudo());
        aviso.setAutor(autor);
        aviso.setDataPublicacao(LocalDateTime.now());
        aviso.setStatus(dto.getStatus());
        aviso.setTipoAtividade(autor.getTipoAtividadeGerenciada());
        aviso.setRelevancia(dto.getRelevancia());
        Aviso novoAviso = avisoRepository.save(aviso);
        return mapToResponseDTO(novoAviso, autor);
    }
    
    @Transactional(readOnly = true)
    public AvisoResponseDTO buscarAvisoPorId(Long id, String userEmail) { /* ... */ return null; }
    @Transactional
    public AvisoResponseDTO editarAviso(Long id, AvisoRequestDTO dto, String userEmail) { /* ... */ return null; }
    @Transactional
    public void deletarAviso(Long id, String userEmail) { /* ... */ }

    private AvisoResponseDTO mapToResponseDTO(Aviso aviso, Usuario usuarioAtual) {
        boolean podeEditar = aviso.getAutor().equals(usuarioAtual);
        boolean lido = aviso.getLidoPorUsuarios().contains(usuarioAtual);
        return new AvisoResponseDTO(aviso.getId(), aviso.getTitulo(), aviso.getConteudo(),
            aviso.getDataPublicacao(), aviso.getAutor().getNome(), aviso.getTipoAtividade(),
            aviso.getStatus(), aviso.getRelevancia(), podeEditar, lido);
    }
}