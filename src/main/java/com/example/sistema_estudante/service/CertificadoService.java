package com.example.sistema_estudante.service;

import com.example.sistema_estudante.dto.*;
import com.example.sistema_estudante.model.*;
import com.example.sistema_estudante.repository.CertificadoRepository;
import com.example.sistema_estudante.repository.ModalidadeRepository;
import com.example.sistema_estudante.repository.SubcategoriaRepository;
import com.example.sistema_estudante.repository.UsuarioRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CertificadoService {

    private static final Logger log = LoggerFactory.getLogger(CertificadoService.class);

    private final CertificadoRepository certificadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final SubcategoriaRepository subcategoriaRepository;
    private final NotificacaoService notificacaoService;
    private final TemplateEngine templateEngine;
    private final GamificacaoService gamificacaoService;

    @Autowired
    public CertificadoService(CertificadoRepository certificadoRepository,
                              UsuarioRepository usuarioRepository,
                              ModalidadeRepository modalidadeRepository,
                              SubcategoriaRepository subcategoriaRepository,
                              NotificacaoService notificacaoService,
                              TemplateEngine templateEngine,
                              GamificacaoService gamificacaoService) {
        this.certificadoRepository = certificadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.modalidadeRepository = modalidadeRepository;
        this.subcategoriaRepository = subcategoriaRepository;
        this.notificacaoService = notificacaoService;
        this.templateEngine = templateEngine;
        this.gamificacaoService = gamificacaoService;
    }

    private String converterPdfParaImagemBase64(String pdfBase64) {
        if (pdfBase64 == null || !pdfBase64.startsWith("data:application/pdf;base64,")) {
            return pdfBase64;
        }
        String base64Data = pdfBase64.substring(pdfBase64.indexOf(',') + 1);
        byte[] pdfBytes = Base64.getDecoder().decode(base64Data);

        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 150);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            log.error("Falha ao converter o arquivo PDF para imagem. Erro: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao processar o arquivo PDF. Verifique o formato do arquivo.", e);
        }
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
        certificado.setFotoBase64(converterPdfParaImagemBase64(certificadoDTO.getFotoBase64()));
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
            certificado.setFotoBase64(converterPdfParaImagemBase64(certificadoDTO.getFotoBase64()));
        }

        certificado.setStatus(CertificadoStatus.PENDENTE);
        certificado.setDataEnvio(LocalDateTime.now());

        Certificado certificadoSalvo = certificadoRepository.save(certificado);
        return toCertificadoDTO(certificadoSalvo);
    }

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
            novoCertificado.setFotoBase64(converterPdfParaImagemBase64(certIndividual.getFotoBase64()));
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
            gamificacaoService.processarAprovacaoCertificado(certificado.getUsuario(), professor);
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
        dto.setUsuarioMatricula(certificado.getUsuario().getMatricula());

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
    
    // --- NOVO MÉTODO PARA O HISTÓRICO ---
    @Transactional(readOnly = true)
    public List<CertificadoDTO> listarCertificadosRevisadosPeloProfessor(String professorEmail) {
        Usuario professor = getUsuarioAutenticado(professorEmail);
        if (professor.getPerfil() != Perfil.PROFESSOR) {
            throw new AccessDeniedException("Apenas professores podem acessar esta funcionalidade.");
        }

        List<Certificado> revisados = certificadoRepository.findByProfessorRevisorOrderByDataRevisaoDesc(professor);

        return revisados.stream()
                .map(this::toCertificadoDTO)
                .collect(Collectors.toList());
    }

    private Usuario getUsuarioAutenticado(String userEmail) {
        return usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + userEmail));
    }
    
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    @Transactional(readOnly = true)
    public byte[] gerarRelatorioDeCertificados(String userEmail) throws Exception {
        Usuario usuario = getUsuarioAutenticado(userEmail);
        List<Certificado> certificados = certificadoRepository.findByUsuario(usuario);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();
        
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Relatório de Certificados de Atividades Complementares", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        Font studentFont = new Font(Font.HELVETICA, 14, Font.NORMAL);
        Paragraph studentName = new Paragraph("Aluno: " + usuario.getNome(), studentFont);
        document.add(studentName);
        document.add(new Paragraph("\n"));

        for (Certificado cert : certificados) {
            Paragraph certTitle = new Paragraph("Título: " + cert.getTitulo());
            certTitle.setFont(new Font(Font.HELVETICA, 12, Font.BOLD));
            document.add(certTitle);

            document.add(new Paragraph("Modalidade: " + cert.getSubcategoria().getModalidade().getNome()));
            document.add(new Paragraph("Atividade: " + cert.getSubcategoria().getDescricao()));
            document.add(new Paragraph("Carga Horária: " + cert.getCargaHoraria() + "h"));
            document.add(new Paragraph("Status: " + cert.getStatus()));
            document.add(new Paragraph("Data de Envio: " + cert.getDataEnvio()));

            if (cert.getObservacoesProfessor() != null && !cert.getObservacoesProfessor().isEmpty()) {
                document.add(new Paragraph("Observações do Professor: " + cert.getObservacoesProfessor()));
            }

            document.add(new Paragraph("\n"));
        }

        document.close();
        return baos.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] gerarCertificadoFinal(String userEmail) throws Exception {
        Usuario usuario = getUsuarioAutenticado(userEmail);
        ProgressoDTO progresso = calcularProgressoAluno(userEmail);

        if (progresso.totalHorasValidadas() < progresso.metaHoras()) {
            throw new IllegalStateException("A meta de horas ainda não foi atingida para gerar o certificado final.");
        }

        List<Certificado> certificadosAprovados = certificadoRepository.findByUsuarioAndStatus(usuario, CertificadoStatus.APROVADO);
        
        Context context = new Context();
        context.setVariable("aluno", usuario);
        context.setVariable("progresso", progresso);
        context.setVariable("certificadosAprovados", certificadosAprovados);
        
        String htmlContent = templateEngine.process("certificado_conclusao", context);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(baos);

        return baos.toByteArray();
    }
}

