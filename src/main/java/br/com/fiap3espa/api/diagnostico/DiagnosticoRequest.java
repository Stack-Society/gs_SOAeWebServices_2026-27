// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Campo obrigatório
import jakarta.validation.constraints.NotNull;

// Mapa chave → valor (questão → alternativa)
import java.util.Map;

/**
 * Respostas do aluno: {"q1": "B", "q2": "A", ..., "q24": "C"}.
 * A série vem do cadastro do aluno; o conteúdo das respostas é conferido pelo CorrecaoService.
 */
public record DiagnosticoRequest(
        // O objeto "respostas" precisa existir no JSON; as chaves e os valores são validados depois
        @NotNull Map<String, String> respostas
) {
}
