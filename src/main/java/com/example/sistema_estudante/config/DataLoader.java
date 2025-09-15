package com.example.sistema_estudante.config;

import com.example.sistema_estudante.model.Medalha;
import com.example.sistema_estudante.model.Perfil;
import com.example.sistema_estudante.repository.MedalhaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final MedalhaRepository medalhaRepository;

    public DataLoader(MedalhaRepository medalhaRepository) {
        this.medalhaRepository = medalhaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Medalha> todasAsMedalhas = new ArrayList<>();

        // --- MEDALHAS PARA ALUNOS (baseadas em Nível) ---
        todasAsMedalhas.addAll(List.of(
            new Medalha(
                "Semente do Saber",
                "Concedida por alcançar o Nível 2. O primeiro passo de uma grande jornada!",
                "🌱",
                Perfil.ALUNO // Atribuído ao perfil ALUNO
            ),
            new Medalha(
                "Guardião dos Certificados",
                "Concedida por alcançar o Nível 5. Seu esforço está sendo notado!",
                "🛡️",
                Perfil.ALUNO // Atribuído ao perfil ALUNO
            ),
            new Medalha(
                "Mestre do Conhecimento",
                "Concedida por alcançar o Nível 10. Você é um exemplo de dedicação!",
                "🏆",
                Perfil.ALUNO // Atribuído ao perfil ALUNO
            ),
            new Medalha(
                "Lenda Acadêmica",
                "Concedida por alcançar o Nível 20. O ápice do conhecimento foi atingido!",
                "💎",
                Perfil.ALUNO // Atribuído ao perfil ALUNO
            )
        ));

        // --- MEDALHAS PARA PROFESSORES (baseadas em revisões) ---
        todasAsMedalhas.addAll(List.of(
            new Medalha(
                "Revisor Iniciante",
                "Concedida por revisar seus primeiros 10 certificados.",
                "✍️",
                Perfil.PROFESSOR
            ),
            new Medalha(
                "Mentor Dedicado",
                "Concedida por revisar 50 certificados e guiar os alunos.",
                "🧑‍🏫",
                Perfil.PROFESSOR
            ),
            new Medalha(
                "Mestre Avaliador",
                "Concedida por revisar 100 certificados com excelência.",
                "🧐",
                Perfil.PROFESSOR
            ),
            new Medalha(
                "Pilar da Academia",
                "Concedida por revisar 200 certificados. Sua contribuição é fundamental!",
                "🏛️",
                Perfil.PROFESSOR
            )
        ));

        // Salva apenas as medalhas que ainda não existem no banco
        for (Medalha medalha : todasAsMedalhas) {
            if (medalhaRepository.findByNome(medalha.getNome()).isEmpty()) {
                medalhaRepository.save(medalha);
            }
        }
    }
}