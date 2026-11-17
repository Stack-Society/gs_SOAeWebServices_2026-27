// Pacote da funcionalidade de alunos: entidade, DTOs, repositório e service
package br.com.fiap3espa.api.aluno;

// Anotações de validação (Bean Validation):
import jakarta.validation.constraints.Max;      // valor máximo permitido
import jakarta.validation.constraints.Min;      // valor mínimo permitido
import jakarta.validation.constraints.NotNull;  // campo obrigatório

/** Atualização de aluno (PUT): série e escola; o código não muda. */
// record: classe imutável com construtor, getters, equals e toString gerados automaticamente
public record AlunoAtualizacaoRequest(
        // Série obrigatória, entre 1 e 5
        @NotNull @Min(1) @Max(5) Integer serie,
        // Id da escola obrigatório (a existência é conferida no AlunoService)
        @NotNull Long escolaId
) {
}
