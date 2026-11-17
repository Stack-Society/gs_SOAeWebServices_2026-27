// Pacote da funcionalidade de escolas: entidade, DTOs, repositório e service
package br.com.fiap3espa.api.escola;

/** Escola devolvida pela API. */
public record EscolaResponse(
        // Id gerado pelo banco
        Long id,
        // Código da escola
        String codigo,
        // Nome da escola
        String nome) {

    // Construtor que converte a entidade Escola no formato de resposta (usado como EscolaResponse::new)
    public EscolaResponse(Escola escola) {
        // Chama o construtor principal do record com os dados da entidade
        this(
                escola.getId(),
                escola.getCodigo(),
                escola.getNome()
        );
    }
}
