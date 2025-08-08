package com.example.sistema_estudante.controller;

import com.example.sistema_estudante.dto.*;
import com.example.sistema_estudante.service.CertificadoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/certificados")
@CrossOrigin(origins = "http://localhost:5173")
public class CertificadoController {

    private final CertificadoService certificadoService;

    public CertificadoController(CertificadoService certificadoService) {
        this.certificadoService = certificadoService;
    }

    // =======================================================
    // ENDPOINTS DE CONSULTA
    // =======================================================

    /**
     * Retorna a lista de todas as modalidades de atividades.
     */
    @GetMapping("/modalidades")
    public ResponseEntity<List<ModalidadeDTO>> getModalidades() {
        List<ModalidadeDTO> modalidades = certificadoService.listarModalidades();
        return ResponseEntity.ok(modalidades);
    }

    /**
     * Retorna a lista de subcategorias para uma modalidade específica.
     */
    @GetMapping("/modalidades/{id}/subcategorias")
    public ResponseEntity<List<SubcategoriaDTO>> getSubcategoriasPorModalidade(@PathVariable Long id) {
        List<SubcategoriaDTO> subcategorias = certificadoService.listarSubcategoriasPorModalidade(id);
        return ResponseEntity.ok(subcategorias);
    }

    // =======================================================
    // ENDPOINTS PARA ALUNOS
    // =======================================================
    
    @PostMapping("/enviar-em-lote")
    public ResponseEntity<?> enviarLotePorSubcategoria(
            @RequestBody LotePorSubcategoriaDTO loteDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<CertificadoDTO> salvos = certificadoService.salvarLotePorSubcategoria(loteDTO, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(salvos);
        } catch (Exception e) {
            // Retorna uma mensagem de erro clara para o front-end
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Envia um único certificado.
     */
    @PostMapping("/enviar")
    public ResponseEntity<CertificadoDTO> enviarCertificado(
            @RequestBody CertificadoDTO certificadoDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        CertificadoDTO salvo = certificadoService.salvarCertificado(certificadoDTO, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    /**
     * Retorna o progresso do aluno com base nas horas validadas.
     */
    @GetMapping("/meus/progresso")
    public ResponseEntity<ProgressoDTO> getProgressoAluno(
            @AuthenticationPrincipal UserDetails userDetails) {
        ProgressoDTO progresso = certificadoService.calcularProgressoAluno(userDetails.getUsername());
        return ResponseEntity.ok(progresso);
    }

    /**
     * Lista todos os certificados enviados pelo aluno autenticado.
     */
    @GetMapping("/meus")
    public ResponseEntity<List<CertificadoDTO>> listarMeusCertificados(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<CertificadoDTO> certificados = certificadoService.listarCertificadosDoAluno(userDetails.getUsername());
        return ResponseEntity.ok(certificados);
    }
    
    /**
     * Permite que o aluno edite um certificado seu.
     */
    @PutMapping("/meus/{id}")
    public ResponseEntity<?> editarCertificado(
            @PathVariable Long id,
            @RequestBody CertificadoDTO certificadoDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            CertificadoDTO atualizado = certificadoService.editarCertificado(id, certificadoDTO, userDetails.getUsername());
            return ResponseEntity.ok(atualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Permite que o aluno exclua um certificado seu.
     */
    @DeleteMapping("/meus/{id}")
    public ResponseEntity<?> deletarCertificado(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            certificadoService.deletarCertificado(id, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * NOVO ENDPOINT: Gera um certificado final de conclusão para o aluno.
     */
    @GetMapping("/gerar-declaracao-final")
    public ResponseEntity<byte[]> gerarCertificadoFinal(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            byte[] pdfBytes = certificadoService.gerarCertificadoFinal(userDetails.getUsername());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "certificado_conclusao.pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * NOVO ENDPOINT: Gera um relatório PDF com todos os certificados do aluno.
     */
    @GetMapping("/meus/relatorio")
    public ResponseEntity<byte[]> gerarRelatorioCertificados(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            byte[] pdfBytes = certificadoService.gerarRelatorioDeCertificados(userDetails.getUsername());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "relatorio_certificados.pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =======================================================
    // ENDPOINTS PARA PROFESSORES
    // =======================================================

    /**
     * Lista certificados pendentes de revisão para o professor autenticado.
     */
    @GetMapping("/revisao/pendentes")
    public ResponseEntity<List<CertificadoDTO>> listarCertificadosParaRevisao(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<CertificadoDTO> certificados = certificadoService.listarCertificadosPendentesParaProfessor(userDetails.getUsername());
        return ResponseEntity.ok(certificados);
    }

    /**
     * Permite que um professor revise (aprovar ou rejeitar) um certificado.
     */
    @PutMapping("/{id}/revisar")
    public ResponseEntity<CertificadoDTO> revisarCertificado(
            @PathVariable Long id,
            @RequestBody CertificadoRevisaoDTO revisaoDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        CertificadoDTO atualizado = certificadoService.revisarCertificado(
                id,
                userDetails.getUsername(),
                revisaoDTO.status(),
                revisaoDTO.observacoesProfessor()
        );
        return ResponseEntity.ok(atualizado);
    }
}