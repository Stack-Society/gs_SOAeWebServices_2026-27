// Pacote do questionário: catálogo (disciplinas, conteúdos e questões), DTOs, repositórios e service
package br.com.fiap3espa.api.questionario;

// Lista
import java.util.List;

/** Questionário de uma série: as 24 questões, sem gabarito. */
public record QuestionarioResponse(
        // Série pedida
        Integer serie,
        // Total de questões (24)
        Integer totalQuestoes,
        // Questões em ordem de número
        List<QuestaoResponse> questoes
) {
}
