// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Lista
import java.util.List;

/**
 * Insights do diagnóstico.
 * conteudos: os 8 conteúdos na ordem do modelo; disciplinas: nível de cada uma;
 * reforcar: lacunas primeiro, depois "a confirmar", cada grupo da maior para a menor probabilidade.
 */
public record Insights(
        // Resultado de cada conteúdo
        List<ResultadoConteudo> conteudos,
        // Nível de cada disciplina
        List<ResultadoDisciplina> disciplinas,
        // Conteúdos a reforçar, em ordem de urgência
        List<ResultadoConteudo> reforcar
) {
}
