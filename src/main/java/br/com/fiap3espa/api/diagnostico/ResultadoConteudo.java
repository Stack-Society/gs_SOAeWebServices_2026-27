// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Entidade do conteúdo avaliado (catálogo do questionário)
import br.com.fiap3espa.api.questionario.Conteudo;

/** Resultado de um conteúdo: acertos, probabilidade de lacuna do modelo e a decisão. */
// record imutável usado entre o InsightService e o DiagnosticoService
public record ResultadoConteudo(Conteudo conteudo, int acertos, double probabilidade, Situacao situacao) {
}
