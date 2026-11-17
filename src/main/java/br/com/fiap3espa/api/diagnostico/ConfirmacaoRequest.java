// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Anotações de validação:
import jakarta.validation.constraints.NotNull;  // campo obrigatório
import jakarta.validation.constraints.Pattern;  // texto deve seguir uma expressão regular

/** Decisão do professor para um conteúdo "a confirmar": "lacuna" ou "sem_lacuna". */
public record ConfirmacaoRequest(
        // Obrigatório e só aceita exatamente "lacuna" ou "sem_lacuna"; senão, 400 com a mensagem abaixo
        @NotNull @Pattern(regexp = "lacuna|sem_lacuna", message = "deve ser lacuna ou sem_lacuna") String situacao
) {
}
