// Pacote da funcionalidade de alunos: entidade, DTOs, repositório e service
package br.com.fiap3espa.api.aluno;

// DTO da escola, usado dentro da resposta do aluno
import br.com.fiap3espa.api.escola.EscolaResponse;

/** Aluno devolvido pela API, com os dados da escola. */
public record AlunoResponse(
        // Id gerado pelo banco
        Long id,
        // Código anônimo
        String codigo,
        // Série atual
        Integer serie,
        // Escola do aluno
        EscolaResponse escola) {

    // Construtor que converte a entidade Aluno no formato de resposta (usado como AlunoResponse::new)
    public AlunoResponse(Aluno aluno) {
        // Chama o construtor principal do record com os dados da entidade
        this(
                aluno.getId(),
                aluno.getCodigo(),
                aluno.getSerie(),
                // Converte a escola em EscolaResponse
                new EscolaResponse(aluno.getEscola())
        );
    }
}
