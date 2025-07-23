package com.example.sistema_estudante.config;

import com.example.sistema_estudante.model.Modalidade;
import com.example.sistema_estudante.model.ModalidadeTipo;
import com.example.sistema_estudante.model.Subcategoria;
import com.example.sistema_estudante.repository.ModalidadeRepository;
import com.example.sistema_estudante.repository.SubcategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ModalidadeRepository modalidadeRepository;
    private final SubcategoriaRepository subcategoriaRepository;

    public DataSeeder(ModalidadeRepository modalidadeRepository, SubcategoriaRepository subcategoriaRepository) {
        this.modalidadeRepository = modalidadeRepository;
        this.subcategoriaRepository = subcategoriaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Verifica se as modalidades já foram inseridas para não duplicar dados
        if (modalidadeRepository.count() == 0) {
            System.out.println(">>> Banco de dados vazio. Populando modalidades e subcategorias...");
            criarModalidadesESubcategorias();
            System.out.println(">>> Dados populados com sucesso!");
        } else {
            System.out.println(">>> Banco de dados já populado. Nenhuma ação necessária.");
        }
    }

    private void criarModalidadesESubcategorias() {
        // 1. Criar e Salvar Modalidade de Pesquisa
        Modalidade pesquisa = new Modalidade();
        pesquisa.setNome("Atividades de Pesquisa");
        pesquisa.setMaxHoras(100);
        pesquisa.setTipo(ModalidadeTipo.PESQUISA);
        modalidadeRepository.save(pesquisa);

        List<Subcategoria> subcategoriasPesquisa = List.of(
            createSub("Apresentação em eventos técnicos-científicos: Regional (oral ou banner)", 15, pesquisa),
            createSub("Apresentação em eventos técnicos-científicos: Nacional", 20, pesquisa),
            createSub("Apresentação em eventos técnicos-científicos: Internacional", 25, pesquisa),
            createSub("Publicações em anais de eventos científicos nacionais: Resumos simples", 10, pesquisa),
            createSub("Publicações em anais de eventos científicos nacionais: Resumos expandidos", 15, pesquisa),
            createSub("Publicações em anais de eventos científicos nacionais: Trabalhos completos", 25, pesquisa),
            createSub("Publicações em anais de eventos internacionais: Resumos simples", 20, pesquisa),
            createSub("Publicações em anais de eventos internacionais: Resumos expandidos", 30, pesquisa),
            createSub("Publicações em anais de eventos internacionais: Trabalhos completos", 40, pesquisa),
            createSub("Artigos completos em periódicos indexados: Nacionais", 40, pesquisa),
            createSub("Artigos completos em periódicos indexados: Internacionais", 50, pesquisa),
            createSub("Participação em projetos de iniciação científica (PIBIC/PIVIC): a cada 6 meses", 30, pesquisa),
            createSub("Participação em projetos de pesquisa: a cada 6 meses", 20, pesquisa)
        );
        subcategoriaRepository.saveAll(subcategoriasPesquisa);

        // 2. Criar e Salvar Modalidade de Extensão
        Modalidade extensao = new Modalidade();
        extensao.setNome("Atividades de Extensão");
        extensao.setMaxHoras(100);
        extensao.setTipo(ModalidadeTipo.EXTENSAO);
        modalidadeRepository.save(extensao);

        List<Subcategoria> subcategoriasExtensao = List.of(
            createSub("Eventos de extensão universitária (congressos, etc): Regional", 10, extensao),
            createSub("Eventos de extensão universitária (congressos, etc): Nacional", 15, extensao),
            createSub("Eventos de extensão universitária (congressos, etc): Internacional", 20, extensao),
            createSub("Membro de comissão organizadora de eventos", 20, extensao),
            createSub("Participação em PET (Programa de Educação Tutorial)", 60, extensao),
            createSub("Publicação de trabalhos/resultados de extensão", 10, extensao),
            createSub("Bolsista ou voluntário em extensão: a cada 6 meses", 20, extensao),
            createSub("Estágios não obrigatórios (mínimo 160h)", 50, extensao),
            createSub("Participação em treinamentos (mínimo 10h): Na área", 20, extensao),
            createSub("Participação em treinamentos (mínimo 10h): Áreas afins", 10, extensao),
            createSub("Atividades esportivas institucionais", 10, extensao),
            createSub("Cursos online em áreas afins (mínimo 25h)", 25, extensao),
            createSub("Programas de intercâmbio interinstitucional", 50, extensao),
            createSub("Grupo de empreendedorismo", 40, extensao)
        );
        subcategoriaRepository.saveAll(subcategoriasExtensao);

        // 3. Criar e Salvar Modalidade de Ensino
        Modalidade ensino = new Modalidade();
        ensino.setNome("Atividades de Ensino");
        ensino.setMaxHoras(100);
        ensino.setTipo(ModalidadeTipo.ENSINO);
        modalidadeRepository.save(ensino);

        List<Subcategoria> subcategoriasEnsino = List.of(
            createSub("Monitoria acadêmica (a cada 6 meses)", 50, ensino),
            createSub("Aprovação em disciplinas optativas na própria UFRA", 35, ensino),
            createSub("Aprovação em disciplinas optativas em outras IFES", 35, ensino)
        );
        subcategoriaRepository.saveAll(subcategoriasEnsino);

        // 4. Criar e Salvar Modalidade de Gestão
        Modalidade gestao = new Modalidade();
        gestao.setNome("Atividades de Gestão e Representação");
        gestao.setMaxHoras(50);
        gestao.setTipo(ModalidadeTipo.GESTAO_REPRESENTACAO);
        modalidadeRepository.save(gestao);

        List<Subcategoria> subcategoriasGestao = List.of(
            createSub("Conselhos superiores (a cada 6 meses)", 10, gestao),
            createSub("Colegiado de curso", 10, gestao),
            createSub("Colegiado de instituto/campus", 10, gestao),
            createSub("Centro acadêmico/diretório estudantil", 10, gestao),
            createSub("Representante de turma", 10, gestao),
            createSub("Atuação como mesário em eleições", 10, gestao),
            createSub("Membro de comissões institucionais (por comissão)", 10, gestao)
        );
        subcategoriaRepository.saveAll(subcategoriasGestao);
    }

    // Método auxiliar para facilitar a criação de subcategorias
    private Subcategoria createSub(String descricao, int horas, Modalidade modalidade) {
        Subcategoria sub = new Subcategoria();
        sub.setDescricao(descricao);
        sub.setHoras(horas);
        sub.setModalidade(modalidade);
        return sub;
    }
}