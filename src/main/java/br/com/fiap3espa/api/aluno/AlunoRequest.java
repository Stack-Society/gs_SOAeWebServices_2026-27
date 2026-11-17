// Pacote da funcionalidade de alunos: entidade, DTOs, repositório e service
package br.com.fiap3espa.api.aluno;

// Anotações de validação (Bean Validation):
import jakarta.validation.constraints.Max;       // valor máximo
import jakarta.validation.constraints.Min;       // valor mínimo
import jakarta.validation.constraints.NotBlank;  // texto obrigatório e não vazio
import jakarta.validation.constraints.NotNull;   // campo obrigatório
import jakarta.validation.constraints.Size;      // tamanho máximo do texto

/** Cadastro de aluno (POST). Só código anônimo: nenhum dado pessoal. */
public record AlunoRequest(
        // Código anônimo: obrigatório e com até 20 caracteres (mesmo limite da coluna)
        @NotBlank @Size(max = 20) String codigo,
        // Série obrigatória, entre 1 e 5
        @NotNull @Min(1) @Max(5) Integer serie,
        // Id da escola obrigatório
        @NotNull Long escolaId
) {
}
