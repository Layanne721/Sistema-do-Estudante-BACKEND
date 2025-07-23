package com.example.sistema_estudante.service;

import com.example.sistema_estudante.dto.*;
import com.example.sistema_estudante.model.*;
import com.example.sistema_estudante.repository.CertificadoRepository;
import com.example.sistema_estudante.repository.ModalidadeRepository;
import com.example.sistema_estudante.repository.SubcategoriaRepository;
import com.example.sistema_estudante.repository.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CertificadoService {

    private final CertificadoRepository certificadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final SubcategoriaRepository subcategoriaRepository;
    private final NotificacaoService notificacaoService;

    public CertificadoService(CertificadoRepository certificadoRepository,
                              UsuarioRepository usuarioRepository,
                              ModalidadeRepository modalidadeRepository,
                              SubcategoriaRepository subcategoriaRepository,
                              NotificacaoService notificacaoService) {
        this.certificadoRepository = certificadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.modalidadeRepository = modalidadeRepository;
        this.subcategoriaRepository = subcategoriaRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional(readOnly = true)
    public List<ModalidadeDTO> listarModalidades() {
        return modalidadeRepository.findAll().stream()
                .map(modalidade -> new ModalidadeDTO(modalidade.getId(), modalidade.getNome(), modalidade.getMaxHoras()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubcategoriaDTO> listarSubcategoriasPorModalidade(Long modalidadeId) {
        return subcategoriaRepository.findByModalidadeId(modalidadeId).stream()
                .map(sub -> new SubcategoriaDTO(sub.getId(), sub.getDescricao(), sub.getHoras()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CertificadoDTO salvarCertificado(CertificadoDTO certificadoDTO, String username) {
        Usuario usuario = getUsuarioAutenticado(username);
        if (usuario.getPerfil() != Perfil.ALUNO) {
            throw new AccessDeniedException("Apenas usuários com perfil de ALUNO podem enviar certificados.");
        }

        Subcategoria subcategoria = subcategoriaRepository.findById(certificadoDTO.getSubcategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoria não encontrada com o ID: " + certificadoDTO.getSubcategoriaId()));

        double cargaHorariaInformada = certificadoDTO.getCargaHoraria();
        if (cargaHorariaInformada <= 0) {
            throw new IllegalArgumentException("A carga horária deve ser maior que zero.");
        }

        Certificado certificado = new Certificado();
        certificado.setTitulo(certificadoDTO.getTitulo());
        certificado.setSubcategoria(subcategoria);
        certificado.setCargaHoraria(cargaHorariaInformada);
        certificado.setFotoBase64(certificadoDTO.getFotoBase64());
        certificado.setDataEnvio(LocalDateTime.now());
        certificado.setStatus(CertificadoStatus.PENDENTE);
        certificado.setUsuario(usuario);

        Certificado salvo = certificadoRepository.save(certificado);
        return toCertificadoDTO(salvo);
    }

    @Transactional
    public CertificadoDTO editarCertificado(Long id, CertificadoDTO certificadoDTO, String userEmail) {
        Usuario usuario = getUsuarioAutenticado(userEmail);
        Certificado certificado = certificadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado não encontrado com o ID: " + id));

        if (!certificado.getUsuario().equals(usuario)) {
            throw new AccessDeniedException("Você não tem permissão para editar este certificado.");
        }
        if (certificado.getStatus() != CertificadoStatus.PENDENTE && certificado.getStatus() != CertificadoStatus.REVISAO_NECESSARIA) {
            throw new IllegalStateException("Este certificado não pode mais ser editado pois já foi " + certificado.getStatus() + ".");
        }

        Subcategoria subcategoria = subcategoriaRepository.findById(certificadoDTO.getSubcategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoria não encontrada com o ID: " + certificadoDTO.getSubcategoriaId()));

        double cargaHorariaInformada = certificadoDTO.getCargaHoraria();
        if (cargaHorariaInformada <= 0) {
            throw new IllegalArgumentException("A carga horária deve ser maior que zero.");
        }

        certificado.setTitulo(certificadoDTO.getTitulo());
        certificado.setSubcategoria(subcategoria);
        certificado.setCargaHoraria(cargaHorariaInformada);

        if (certificadoDTO.getFotoBase64() != null && !certificadoDTO.getFotoBase64().isEmpty()) {
            certificado.setFotoBase64(certificadoDTO.getFotoBase64());
        }

        certificado.setStatus(CertificadoStatus.PENDENTE);
        certificado.setDataEnvio(LocalDateTime.now());

        Certificado certificadoSalvo = certificadoRepository.save(certificado);
        return toCertificadoDTO(certificadoSalvo);
    }

    // MÉTODO CORRIGIDO E RENOMEADO
    @Transactional
    public List<CertificadoDTO> salvarLotePorSubcategoria(LotePorSubcategoriaDTO loteDTO, String username) {
        Usuario usuario = getUsuarioAutenticado(username);
        if (usuario.getPerfil() != Perfil.ALUNO) {
            throw new AccessDeniedException("Apenas usuários com perfil de ALUNO podem enviar certificados.");
        }

        Subcategoria subcategoria = subcategoriaRepository.findById(loteDTO.getSubcategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoria não encontrada com o ID: " + loteDTO.getSubcategoriaId()));

        List<Certificado> certificadosParaSalvar = new ArrayList<>();

        for (CertificadoIndividualDTO certIndividual : loteDTO.getCertificados()) {
            if (certIndividual.getCargaHoraria() <= 0) {
                throw new IllegalArgumentException("A carga horária do certificado '" + certIndividual.getTitulo() + "' deve ser maior que zero.");
            }

            Certificado novoCertificado = new Certificado();
            novoCertificado.setTitulo(certIndividual.getTitulo());
            novoCertificado.setCargaHoraria(certIndividual.getCargaHoraria());
            novoCertificado.setFotoBase64(certIndividual.getFotoBase64());
            
            novoCertificado.setSubcategoria(subcategoria);
            novoCertificado.setUsuario(usuario);
            novoCertificado.setStatus(CertificadoStatus.PENDENTE);
            novoCertificado.setDataEnvio(LocalDateTime.now());
            
            certificadosParaSalvar.add(novoCertificado);
        }

        List<Certificado> certificadosSalvos = certificadoRepository.saveAll(certificadosParaSalvar);

        return certificadosSalvos.stream()
                .map(this::toCertificadoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProgressoDTO calcularProgressoAluno(String userEmail) {
        final int META_HORAS_TOTAIS = 200;
        Usuario usuario = getUsuarioAutenticado(userEmail);

        List<Certificado> aprovados = certificadoRepository.findByUsuarioAndStatus(usuario, CertificadoStatus.APROVADO);

        double totalHorasBrutasAcumulado = aprovados.stream().mapToDouble(Certificado::getCargaHoraria).sum();

        Map<Subcategoria, Double> horasValidadasPorSubcategoria = aprovados.stream()
            .collect(Collectors.groupingBy(
                Certificado::getSubcategoria,
                Collectors.summingDouble(Certificado::getCargaHoraria)
            ))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> Math.min(entry.getValue(), entry.getKey().getHoras())
            ));

        Map<Modalidade, Double> horasBrutasPorModalidade = horasValidadasPorSubcategoria.entrySet().stream()
            .collect(Collectors.groupingBy(
                entry -> entry.getKey().getModalidade(),
                Collectors.summingDouble(Map.Entry::getValue)
            ));

        Map<String, CategoriaProgressoDTO> progressoPorCategoria = modalidadeRepository.findAll().stream()
            .collect(Collectors.toMap(
                Modalidade::getNome,
                modalidade -> {
                    double horasBrutasMod = horasBrutasPorModalidade.getOrDefault(modalidade, 0.0);
                    double horasValidadasMod = Math.min(horasBrutasMod, modalidade.getMaxHoras());
                    return new CategoriaProgressoDTO(horasValidadasMod, horasBrutasMod, modalidade.getMaxHoras());
                }
            ));

        double totalHorasValidadasAcumulado = progressoPorCategoria.values().stream()
                                                .mapToDouble(CategoriaProgressoDTO::horasValidadas).sum();

        double totalHorasValidadasFinal = Math.min(totalHorasValidadasAcumulado, META_HORAS_TOTAIS);

        return new ProgressoDTO(
            totalHorasValidadasFinal,
            totalHorasBrutasAcumulado,
            META_HORAS_TOTAIS,
            progressoPorCategoria
        );
    }

    @Transactional
    public CertificadoDTO revisarCertificado(Long certificadoId, String professorEmail, CertificadoStatus novoStatus, String observacoes) {
        Usuario professor = getUsuarioAutenticado(professorEmail);
        if (professor.getPerfil() != Perfil.PROFESSOR) {
            throw new AccessDeniedException("Apenas professores podem revisar certificados.");
        }
        Certificado certificado = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado não encontrado com o ID: " + certificadoId));

        ModalidadeTipo modalidadeDoCertificado = certificado.getSubcategoria().getModalidade().getTipo();
        if (professor.getTipoAtividadeGerenciada() != modalidadeDoCertificado) {
            throw new AccessDeniedException("Você não tem permissão para revisar certificados da modalidade " + modalidadeDoCertificado.name());
        }

        certificado.setStatus(novoStatus);
        certificado.setObservacoesProfessor(observacoes);
        certificado.setProfessorRevisor(professor);
        certificado.setDataRevisao(LocalDateTime.now());

        Certificado certificadoSalvo = certificadoRepository.save(certificado);

        String mensagem;
        String cor;
        if (novoStatus == CertificadoStatus.APROVADO) {
            mensagem = "Seu certificado '" + certificado.getTitulo() + "' foi aprovado!";
            cor = "#28a745";
        } else if (novoStatus == CertificadoStatus.REPROVADO) {
            mensagem = "Seu certificado '" + certificado.getTitulo() + "' foi reprovado.";
            cor = "#dc3545";
        } else {
            mensagem = "Seu certificado '" + certificado.getTitulo() + "' precisa de revisão. Verifique as observações.";
            cor = "#ffc107";
        }
        notificacaoService.criarNotificacao(certificado.getUsuario(), mensagem, cor, "/meus-certificados");

        return toCertificadoDTO(certificadoSalvo);
    }

    private CertificadoDTO toCertificadoDTO(Certificado certificado) {
        CertificadoDTO dto = new CertificadoDTO();
        dto.setId(certificado.getId());
        dto.setTitulo(certificado.getTitulo());
        dto.setCargaHoraria(certificado.getCargaHoraria());
        dto.setFotoBase64(certificado.getFotoBase64());
        dto.setDataEnvio(certificado.getDataEnvio());
        dto.setDataRevisao(certificado.getDataRevisao());
        dto.setObservacoesProfessor(certificado.getObservacoesProfessor());
        dto.setStatus(certificado.getStatus());
        dto.setUsuarioId(certificado.getUsuario().getId());
        dto.setUsuarioNome(certificado.getUsuario().getNome());

        if (certificado.getProfessorRevisor() != null) {
            dto.setProfessorRevisorId(certificado.getProfessorRevisor().getId());
            dto.setProfessorRevisorNome(certificado.getProfessorRevisor().getNome());
        }

        if (certificado.getSubcategoria() != null) {
            dto.setSubcategoriaId(certificado.getSubcategoria().getId());
            dto.setSubcategoriaDescricao(certificado.getSubcategoria().getDescricao());
            if (certificado.getSubcategoria().getModalidade() != null) {
                dto.setModalidadeNome(certificado.getSubcategoria().getModalidade().getNome());
            }
        }

        return dto;
    }

    @Transactional
    public void deletarCertificado(Long id, String userEmail) {
        Usuario usuario = getUsuarioAutenticado(userEmail);
        Certificado certificado = certificadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado não encontrado com o ID: " + id));
        if (!certificado.getUsuario().equals(usuario)) {
            throw new AccessDeniedException("Você não tem permissão para deletar este certificado.");
        }
        if (certificado.getStatus() == CertificadoStatus.APROVADO) {
            throw new IllegalStateException("Este certificado não pode ser deletado pois já foi APROVADO.");
        }
        certificadoRepository.delete(certificado);
    }

    @Transactional(readOnly = true)
    public List<CertificadoDTO> listarCertificadosDoAluno(String username) {
        Usuario usuario = getUsuarioAutenticado(username);
        return certificadoRepository.findByUsuario(usuario)
                .stream()
                .map(this::toCertificadoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CertificadoDTO> listarCertificadosPendentesParaProfessor(String professorEmail) {
        Usuario professor = getUsuarioAutenticado(professorEmail);
        if (professor.getPerfil() != Perfil.PROFESSOR) {
            throw new AccessDeniedException("Apenas professores podem acessar esta funcionalidade.");
        }

        List<Certificado> todosPendentes = certificadoRepository.findByStatusInOrderByDataEnvioDesc(List.of(CertificadoStatus.PENDENTE));

        return todosPendentes.stream()
            .filter(certificado -> certificado.getSubcategoria().getModalidade().getTipo() == professor.getTipoAtividadeGerenciada())
            .map(this::toCertificadoDTO)
            .collect(Collectors.toList());
    }

    private Usuario getUsuarioAutenticado(String userEmail) {
        return usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + userEmail));
    }
    
    // Supondo que você tenha uma exceção customizada para recursos não encontrados
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}