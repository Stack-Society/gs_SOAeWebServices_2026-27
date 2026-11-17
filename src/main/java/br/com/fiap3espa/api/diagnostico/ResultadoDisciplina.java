// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Entidades do catálogo do questionário
import br.com.fiap3espa.api.questionario.Conteudo;
import br.com.fiap3espa.api.questionario.Disciplina;

// Lista
import java.util.List;

/** Nível do aluno em uma disciplina, com os conteúdos em lacuna e os que o professor deve confirmar. */
public record ResultadoDisciplina(
        // Disciplina avaliada
        Disciplina disciplina,
        // Nível calculado
        NivelDisciplina nivel,
        // Conteúdos com lacuna
        List<Conteudo> lacunas,
        // Conteúdos a confirmar
        List<Conteudo> aConfirmar
) {
}
