package com.example.sistema_estudante.service;

import com.example.sistema_estudante.model.CertificadoStatus;
import com.example.sistema_estudante.model.Medalha;
import com.example.sistema_estudante.model.Perfil;
import com.example.sistema_estudante.model.Usuario;
import com.example.sistema_estudante.repository.CertificadoRepository;
import com.example.sistema_estudante.repository.MedalhaRepository;
import com.example.sistema_estudante.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GamificacaoService {

    private static final Logger log = LoggerFactory.getLogger(GamificacaoService.class);

    // --- CONSTANTES DE GAMIFICAÇÃO ---
    private static final int XP_POR_CERTIFICADO_APROVADO = 50;
    private static final int XP_POR_REVISAO_PROFESSOR = 15;
    private static final int XP_BASE_PARA_PROXIMO_NIVEL = 100;

    private final UsuarioRepository usuarioRepository;
    private final MedalhaRepository medalhaRepository;
    private final NotificacaoService notificacaoService;
    private final CertificadoRepository certificadoRepository;

    public GamificacaoService(UsuarioRepository usuarioRepository, MedalhaRepository medalhaRepository, NotificacaoService notificacaoService, CertificadoRepository certificadoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.medalhaRepository = medalhaRepository;
        this.notificacaoService = notificacaoService;
        this.certificadoRepository = certificadoRepository;
    }

    /**
     * Método principal que é chamado quando um certificado é aprovado.
     * Ele orquestra a adição de XP e a verificação de medalhas para o aluno e o professor.
     */
    @Transactional
    public void processarAprovacaoCertificado(Usuario aluno, Usuario professor) {
        log.info("Processando gamificação para aprovação. Aluno: {}, Professor: {}", aluno.getEmail(), professor.getEmail());
        adicionarXpEVerificarConquistasAluno(aluno);
        adicionarXpEVerificarConquistasProfessor(professor);
    }

    /**
     * Adiciona XP ao aluno, verifica se ele subiu de nível e se ganhou novas medalhas.
     */
    private void adicionarXpEVerificarConquistasAluno(Usuario aluno) {
        aluno.setXp(aluno.getXp() + XP_POR_CERTIFICADO_APROVADO);
        log.info("Aluno {} ganhou {} XP. XP total: {}", aluno.getEmail(), XP_POR_CERTIFICADO_APROVADO, aluno.getXp());

        verificarEAtualizarNivel(aluno);
        verificarMedalhasPorNivelParaAluno(aluno);

        usuarioRepository.save(aluno);
    }

    /**
     * Adiciona XP ao professor, verifica se ele subiu de nível e se ganhou novas medalhas por revisão.
     */
    private void adicionarXpEVerificarConquistasProfessor(Usuario professor) {
        professor.setXp(professor.getXp() + XP_POR_REVISAO_PROFESSOR);
        log.info("Professor {} ganhou {} XP. XP total: {}", professor.getEmail(), XP_POR_REVISAO_PROFESSOR, professor.getXp());

        verificarEAtualizarNivel(professor);
        verificarMedalhasPorRevisaoParaProfessor(professor);

        usuarioRepository.save(professor);
    }

    /**
     * Lógica para verificar se um usuário (aluno ou professor) atingiu XP suficiente para subir de nível.
     */
    private void verificarEAtualizarNivel(Usuario usuario) {
        int metaXpParaProximoNivel = usuario.getNivel() * XP_BASE_PARA_PROXIMO_NIVEL;
        if (usuario.getXp() >= metaXpParaProximoNivel) {
            int novoNivel = usuario.getNivel() + 1;
            usuario.setNivel(novoNivel);
            usuario.setXp(usuario.getXp() - metaXpParaProximoNivel); // Reinicia o XP para o novo nível

            log.info("Usuário {} subiu para o Nível {}!", usuario.getEmail(), novoNivel);

            // Cria uma notificação para o usuário
            String mensagem = "Parabéns! Você alcançou o Nível " + novoNivel + "!";
            notificacaoService.criarNotificacao(usuario, mensagem, "#17a2b8", "/meu-perfil");
        }
    }

    /**
     * Verifica se o aluno atingiu um nível que concede uma medalha.
     */
    private void verificarMedalhasPorNivelParaAluno(Usuario aluno) {
        Map<Integer, String> medalhasPorNivel = Map.of(
            2, "Semente do Saber",
            5, "Guardião dos Certificados",
            10, "Mestre do Conhecimento",
            20, "Lenda Acadêmica"
        );

        String nomeMedalha = medalhasPorNivel.get(aluno.getNivel());
        if (nomeMedalha != null) {
            concederMedalha(aluno, nomeMedalha);
        }
    }

    /**
     * Verifica se o professor atingiu um número de revisões que concede uma medalha.
     */
    private void verificarMedalhasPorRevisaoParaProfessor(Usuario professor) {
        long revisoesAprovadas = certificadoRepository.countByProfessorRevisorAndStatusIn(
            professor, List.of(CertificadoStatus.APROVADO, CertificadoStatus.REPROVADO, CertificadoStatus.REVISAO_NECESSARIA)
        );

        Map<Long, String> medalhasPorRevisoes = Map.of(
            10L, "Revisor Iniciante",
            50L, "Mentor Dedicado",
            100L, "Mestre Avaliador",
            200L, "Pilar da Academia"
        );
        
        // Verifica se o número de revisões corresponde a alguma meta de medalha
        String nomeMedalha = medalhasPorRevisoes.get(revisoesAprovadas);
        if (nomeMedalha != null) {
            concederMedalha(professor, nomeMedalha);
        }
    }


    /**
     * Concede uma medalha a um usuário, se ele ainda não a possuir.
     */
    private void concederMedalha(Usuario usuario, String nomeMedalha) {
        boolean usuarioJaPossuiMedalha = usuario.getMedalhas().stream()
                .anyMatch(m -> m.getNome().equals(nomeMedalha));

        if (!usuarioJaPossuiMedalha) {
            Optional<Medalha> medalhaOptional = medalhaRepository.findByNome(nomeMedalha);
            if (medalhaOptional.isPresent()) {
                Medalha medalha = medalhaOptional.get();
                // Garante que o perfil da medalha corresponde ao perfil do usuário
                if (medalha.getPerfilAlvo() == usuario.getPerfil()) {
                    usuario.getMedalhas().add(medalha);
                    log.info("Medalha '{}' concedida para o usuário {}!", nomeMedalha, usuario.getEmail());

                    // Notifica o usuário sobre a nova medalha
                    String mensagem = "Você conquistou uma nova medalha: " + nomeMedalha + "!";
                    notificacaoService.criarNotificacao(usuario, mensagem, "#ffc107", "/meu-perfil");
                }
            } else {
                log.warn("Tentativa de conceder uma medalha não encontrada no banco de dados: {}", nomeMedalha);
            }
        }
    }
}
