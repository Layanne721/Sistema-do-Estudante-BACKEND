package com.example.sistema_estudante.controller;

import com.example.sistema_estudante.dto.AvisoRequestDTO;
import com.example.sistema_estudante.dto.AvisoResponseDTO;
import com.example.sistema_estudante.service.AvisoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/avisos")
@CrossOrigin(origins = "http://localhost:5173")
public class AvisoController {

    private final AvisoService avisoService;

    public AvisoController(AvisoService avisoService) {
        this.avisoService = avisoService;
    }

    // Endpoint para buscar avisos não lidos (a rota principal)
    @GetMapping
    public ResponseEntity<List<AvisoResponseDTO>> buscarAvisosNaoLidos(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<AvisoResponseDTO> avisos = avisoService.buscarNaoLidos(userDetails.getUsername());
        return ResponseEntity.ok(avisos);
    }

    // ✅ CORREÇÃO: A rota específica com texto fixo ("/historico")
    // foi movida para ANTES da rota genérica com variável ("/{id}").
    @GetMapping("/historico")
    public ResponseEntity<List<AvisoResponseDTO>> buscarHistorico(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<AvisoResponseDTO> avisos = avisoService.buscarHistorico(userDetails.getUsername());
        return ResponseEntity.ok(avisos);
    }
    
    // A rota genérica com variável ("/{id}") agora vem DEPOIS.
    @GetMapping("/{id}")
    public ResponseEntity<AvisoResponseDTO> buscarAvisoPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            AvisoResponseDTO aviso = avisoService.buscarAvisoPorId(id, userDetails.getUsername());
            return ResponseEntity.ok(aviso);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/{id}/ler")
    public ResponseEntity<Void> marcarComoLido(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        avisoService.marcarComoLido(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping
    public ResponseEntity<AvisoResponseDTO> criarAviso(@RequestBody AvisoRequestDTO dto, @AuthenticationPrincipal UserDetails userDetails) {
        AvisoResponseDTO novoAviso = avisoService.criarAviso(dto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAviso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvisoResponseDTO> editarAviso(@PathVariable Long id, @RequestBody AvisoRequestDTO dto, @AuthenticationPrincipal UserDetails userDetails) {
        AvisoResponseDTO avisoEditado = avisoService.editarAviso(id, dto, userDetails.getUsername());
        return ResponseEntity.ok(avisoEditado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAviso(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        avisoService.deletarAviso(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}