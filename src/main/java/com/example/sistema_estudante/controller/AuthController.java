package com.example.sistema_estudante.controller;

import com.example.sistema_estudante.dto.*;
import com.example.sistema_estudante.model.Perfil;
import com.example.sistema_estudante.model.Usuario;
import com.example.sistema_estudante.security.JwtService;
import com.example.sistema_estudante.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

            if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
                usuarioService.atualizarAvatarUrl(novoUsuario.getEmail(), request.getAvatarUrl());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Usuário registrado com sucesso: " + novoUsuario.getEmail()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
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
            String avatarUrlResponse = usuarioService.construirAvatarUrl(usuario);

            return ResponseEntity.ok(new LoginResponseDTO(
                token,
                usuario.getNome(),
                usuario.getPerfil(),
                usuario.getPerfil() == Perfil.PROFESSOR ? usuario.getTipoAtividadeGerenciada() : null,
                avatarUrlResponse
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciais inválidas."));
        }
    }

    @GetMapping("/meu-perfil")
    public ResponseEntity<PerfilDTO> getMeuPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = userDetails.getUsername();
        PerfilDTO perfilDTO = usuarioService.getPerfilDoUsuario(email);
        return ResponseEntity.ok(perfilDTO);
    }

    @PutMapping("/meu-perfil/avatar")
    public ResponseEntity<?> updateAvatar(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> payload) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String userEmail = userDetails.getUsername();
            String newAvatarUrl = payload.get("avatarUrl");
            
            usuarioService.atualizarAvatarUrl(userEmail, newAvatarUrl);
            
            return ResponseEntity.ok(Map.of(
                "message", "Avatar atualizado com sucesso", 
                "avatarUrl", newAvatarUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Falha ao atualizar o avatar."));
        }
    }
}