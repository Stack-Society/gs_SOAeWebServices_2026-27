// Pacote da funcionalidade de escolas: entidade, DTOs, repositório e service
package br.com.fiap3espa.api.escola;

// Anotações de validação:
import jakarta.validation.constraints.NotBlank;  // texto obrigatório e não vazio
import jakarta.validation.constraints.Size;      // tamanho máximo

/** Cadastro de escola (POST). */
public record EscolaRequest(
        // Código: obrigatório e com até 20 caracteres
        @NotBlank @Size(max = 20) String codigo,
        // Nome: obrigatório e com até 100 caracteres
        @NotBlank @Size(max = 100) String nome
) {
}
