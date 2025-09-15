package com.example.sistema_estudante.service;

import com.example.sistema_estudante.dto.MedalhaDTO;
import com.example.sistema_estudante.dto.PerfilDTO;
import com.example.sistema_estudante.dto.UsuarioRequestDTO;
import com.example.sistema_estudante.model.Perfil;
import com.example.sistema_estudante.model.Usuario;
import com.example.sistema_estudante.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int META_XP_ALUNO = 200;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario criarUsuario(UsuarioRequestDTO request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("O e-mail fornecido já está em uso.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(request.getNome());
        novoUsuario.setEmail(request.getEmail());
        novoUsuario.setSenha(passwordEncoder.encode(request.getSenha()));
        novoUsuario.setPerfil(request.getPerfil());
        novoUsuario.setDataCadastro(LocalDate.now());

        // --- LÓGICA DE AVATAR PADRÃO CORRIGIDA E MELHORADA ---
        if (request.getAvatarUrl() == null || request.getAvatarUrl().trim().isEmpty()) {
            String nomeParaSeed = novoUsuario.getNome().replaceAll("\\s+", ""); 
            if (request.getPerfil() == Perfil.PROFESSOR) {
                // Para professores, usa o mesmo serviço (DiceBear) mas com um estilo diferente e mais profissional ('micah').
                novoUsuario.setAvatarFilename("https://api.dicebear.com/8.x/micah/svg?seed=" + nomeParaSeed);
            } else {
                // Mantém o estilo 'adventurer' para alunos.
                novoUsuario.setAvatarFilename("https://api.dicebear.com/8.x/adventurer/svg?seed=" + nomeParaSeed);
            }
        } else {
            novoUsuario.setAvatarFilename(request.getAvatarUrl());
        }
        // --- FIM DA LÓGICA DE AVATAR ---

        if (request.getPerfil() == Perfil.ALUNO) {
            novoUsuario.setMatricula(request.getMatricula());
        } else if (request.getPerfil() == Perfil.PROFESSOR) {
            novoUsuario.setCodigoDisciplina(request.getCodigoDisciplina());
            novoUsuario.setTipoAtividadeGerenciada(request.getTipoAtividadeGerenciada());
        }

        return usuarioRepository.save(novoUsuario);
    }
    
    @Transactional(readOnly = true)
    public PerfilDTO getPerfilDoUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        Set<MedalhaDTO> medalhasDTO = usuario.getMedalhas().stream()
                .map(m -> new MedalhaDTO(m.getNome(), m.getDescricao(), m.getImagemUrl()))
                .collect(Collectors.toSet());
        
        String avatarUrl = construirAvatarUrl(usuario);

        // CORREÇÃO: Construtor ajustado para o novo PerfilDTO com 7 campos.
        return new PerfilDTO(
            usuario.getNome(),
            usuario.getEmail(),
            avatarUrl,
            usuario.getNivel(),
            usuario.getXp(),
            META_XP_ALUNO,
            medalhasDTO
        );
    }

    @Transactional
    public void atualizarAvatarUrl(String email, String avatarUrl) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
        
        usuario.setAvatarFilename(avatarUrl);
        usuarioRepository.save(usuario);
    }

    public String construirAvatarUrl(Usuario usuario) {
        String filename = usuario.getAvatarFilename();
        if (filename == null || filename.trim().isEmpty() || "null".equalsIgnoreCase(filename)) {
            return null;
        }
        if (filename.startsWith("http")) {
            return filename;
        }
        if (filename.startsWith("/avatares")) {
             return filename;
        }
        return null;
    }
}

