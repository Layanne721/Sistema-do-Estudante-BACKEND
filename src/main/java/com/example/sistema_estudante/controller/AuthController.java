package com.example.sistema_estudante.controller;

import com.example.sistema_estudante.dto.ForgotPasswordRequest; // Mantido para compatibilidade, mas o novo será usado
import com.example.sistema_estudante.dto.LoginRequestDTO;
import com.example.sistema_estudante.dto.LoginResponseDTO;
import com.example.sistema_estudante.dto.ResetPasswordRequest;
import com.example.sistema_estudante.dto.UsuarioRequestDTO;
import com.example.sistema_estudante.model.Perfil;
import com.example.sistema_estudante.model.Usuario;
import com.example.sistema_estudante.security.JwtService;
import com.example.sistema_estudante.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UsuarioService usuarioService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UsuarioRequestDTO request) {
        try {
            Usuario novoUsuario = usuarioService.criarUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Usuário registrado com sucesso: " + novoUsuario.getEmail()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erro ao registrar usuário: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
            );

            Usuario usuario = (Usuario) authentication.getPrincipal();
            String token = jwtService.generateToken(usuario);

            return ResponseEntity.ok(new LoginResponseDTO(
                token,
                usuario.getNome(),
                usuario.getPerfil(),
                usuario.getPerfil() == Perfil.PROFESSOR ? usuario.getTipoAtividadeGerenciada() : null
            ));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciais inválidas."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciais inválidas."));
        }
    }

    /**
     * Endpoint para solicitar a recuperação de senha.
     * URL corrigida para corresponder ao front-end.
     */
    @PostMapping("/recuperar-senha")
    public ResponseEntity<?> recuperarSenha(@RequestBody ForgotPasswordRequest request) {
        try {
            usuarioService.requestPasswordReset(request.getEmail());
            // Sempre retorna sucesso para não revelar se um e-mail existe no sistema.
            return ResponseEntity.ok(Map.of("message", "Se um usuário com o e-mail informado existir, um link para redefinição de senha foi enviado."));
        } catch (Exception e) {
            // Log do erro real no servidor
            System.err.println("Erro crítico no processo de recuperação de senha: " + e.getMessage());
            // Retorna uma mensagem genérica para o usuário
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Ocorreu um erro interno. Tente novamente mais tarde."));
        }
    }

    /**
     * Endpoint para efetivamente redefinir a senha com um token válido.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            boolean success = usuarioService.resetPassword(request.getToken(), request.getNewPassword());
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Token inválido, expirado ou senha inválida."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao redefinir senha: " + e.getMessage()));
        }
    }
}