package com.example.sistema_estudante.service;

import com.example.sistema_estudante.dto.UsuarioRequestDTO;
import com.example.sistema_estudante.model.Perfil;
import com.example.sistema_estudante.model.Usuario;
import com.example.sistema_estudante.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailgunEmailService emailService; // Certifique-se que esta classe existe

    @Value("${app.frontend.url}")
    private String frontendBaseUrl;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, MailgunEmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public Usuario criarUsuario(UsuarioRequestDTO dados) {
        if (usuarioRepository.findByEmail(dados.getEmail()).isPresent()) {
            throw new IllegalStateException("O e-mail informado já está em uso.");
        }

        if (dados.getPerfil() != Perfil.ALUNO && dados.getPerfil() != Perfil.PROFESSOR) {
            throw new IllegalArgumentException("Perfil de usuário inválido. Apenas ALUNO ou PROFESSOR são permitidos.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dados.getNome());
        novoUsuario.setEmail(dados.getEmail());
        novoUsuario.setPerfil(dados.getPerfil());
        novoUsuario.setDataCadastro(LocalDate.now());
        novoUsuario.setSenha(passwordEncoder.encode(dados.getSenha()));

        if (dados.getPerfil() == Perfil.ALUNO) {
            novoUsuario.setMatricula(dados.getMatricula());
            novoUsuario.setCodigoDisciplina(null);
            novoUsuario.setTipoAtividadeGerenciada(null);
        } else if (dados.getPerfil() == Perfil.PROFESSOR) {
            novoUsuario.setMatricula(null);
            novoUsuario.setCodigoDisciplina(dados.getCodigoDisciplina());
            novoUsuario.setTipoAtividadeGerenciada(dados.getTipoAtividadeGerenciada());
        }

        return usuarioRepository.save(novoUsuario);
    }

    @Transactional
    public boolean requestPasswordReset(String email) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isEmpty()) {
            System.out.println("Tentativa de recuperação de senha para email não encontrado: " + email);
            return true;
        }

        Usuario usuario = usuarioOptional.get();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

        usuario.setResetToken(token);
        usuario.setResetTokenExpiresAt(expiryDate);
        usuarioRepository.save(usuario);

        String resetLink = frontendBaseUrl + "/reset-password?token=" + token;

        return emailService.sendPasswordResetEmail(usuario.getEmail(), usuario.getNome(), resetLink);
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByResetToken(token);

        if (usuarioOptional.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioOptional.get();

        if (usuario.getResetTokenExpiresAt() == null || usuario.getResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            usuario.setResetToken(null);
            usuario.setResetTokenExpiresAt(null);
            usuarioRepository.save(usuario);
            return false;
        }

        usuario.setSenha(passwordEncoder.encode(newPassword));

        usuario.setResetToken(null);
        usuario.setResetTokenExpiresAt(null);
        usuarioRepository.save(usuario);

        return true;
    }
}