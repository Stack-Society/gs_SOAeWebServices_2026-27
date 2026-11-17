// Pacote do questionário: catálogo (disciplinas, conteúdos e questões), DTOs, repositórios e service
package br.com.fiap3espa.api.questionario;

/** Uma questão do questionário, sem o gabarito. */
public record QuestaoResponse(
        // Número da questão (1 a 24)
        Integer numero,
        // Código do conteúdo avaliado
        String conteudo,
        // Nome do conteúdo
        String conteudoNome,
        // Nome da disciplina
        String disciplina
) {
    // Construtor que converte a entidade Questao (usado como QuestaoResponse::new);
    // o gabarito não é copiado, para não ser exposto ao aluno
    public QuestaoResponse(Questao questao) {
        // Chama o construtor principal do record com os dados da entidade
        this(
                questao.getNumero(),
                questao.getConteudo().getCodigo(),
                questao.getConteudo().getNome(),
                questao.getConteudo().getDisciplina().getNome()
        );
    }
}
