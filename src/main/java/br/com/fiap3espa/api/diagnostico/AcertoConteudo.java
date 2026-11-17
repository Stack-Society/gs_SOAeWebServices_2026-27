// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Entidade do conteúdo avaliado
import br.com.fiap3espa.api.questionario.Conteudo;

/** Quantas questões (0 a 3) o aluno acertou em um conteúdo. */
// record imutável: conteúdo + quantidade de acertos
public record AcertoConteudo(Conteudo conteudo, int acertos) {
}
