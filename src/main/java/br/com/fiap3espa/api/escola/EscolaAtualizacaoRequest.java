// Pacote da funcionalidade de escolas: entidade, DTOs, repositório e service
package br.com.fiap3espa.api.escola;

// Anotações de validação:
import jakarta.validation.constraints.NotBlank;  // texto obrigatório e não vazio
import jakarta.validation.constraints.Size;      // tamanho máximo

/** Atualização de escola (PUT): o código não muda. */
public record EscolaAtualizacaoRequest(
        // Novo nome: obrigatório e com até 100 caracteres
        @NotBlank @Size(max = 100) String nome
) {
}
