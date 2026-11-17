// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Classes do Java:
import java.math.BigDecimal;        // decimal exato
import java.math.RoundingMode;      // regra de arredondamento
import java.time.LocalDateTime;     // data e hora
import java.util.Comparator;        // critérios de ordenação
import java.util.List;              // lista

/**
 * Diagnóstico com os insights: nível por disciplina, situação de cada conteúdo e conteúdos a reforçar.
 * Montado a partir do diagnóstico gravado: o POST e o GET devolvem exatamente o mesmo formato.
 */
public record DiagnosticoResponse(
        // Id do diagnóstico
        Long id,
        // Código anônimo do aluno
        String aluno,
        // Série usada na correção
        Integer serie,
        // Data e hora do diagnóstico
        LocalDateTime dataHora,
        // Versão do modelo de IA
        String versaoModelo,
        // Resumo em texto (ex.: "3 lacuna(s) encontrada(s).")
        String mensagem,
        // Nível de cada disciplina
        List<DisciplinaResultado> disciplinas,
        // Resultado de cada um dos 8 conteúdos
        List<ConteudoResultado> conteudos,
        // Conteúdos a reforçar, em ordem de urgência
        List<Reforco> reforcar
) {

    // Resultado de uma disciplina: nível, orientação e listas de conteúdos com lacuna e a confirmar
    public record DisciplinaResultado(
            String disciplina, String nome, String nivel, String orientacao,
            List<String> lacunas, List<String> aConfirmar) {

        // Construtor que monta o resultado de uma disciplina a partir do nível gravado e dos conteúdos
        public DisciplinaResultado(DiagnosticoDisciplina d, List<DiagnosticoConteudo> conteudos) {
            this(
                    // Código e nome da disciplina
                    d.getDisciplina().getCodigo(), d.getDisciplina().getNome(),
                    // Texto e orientação do nível
                    d.getNivel().getCodigo(), d.getNivel().getOrientacao(),
                    // Conteúdos da disciplina com lacuna e com dúvida
                    codigos(conteudos, d, Situacao.LACUNA), codigos(conteudos, d, Situacao.A_CONFIRMAR));
        }
    }

    /** situacao = decisão do modelo; situacaoProfessor = confirmação (ou null); situacaoFinal = a que vale. */
    public record ConteudoResultado(
            String conteudo, String nome, String disciplina,
            Integer acertos, BigDecimal probabilidade, String situacao,
            String situacaoProfessor, String situacaoFinal) {

        // Construtor que converte o resultado gravado de um conteúdo (usado como ConteudoResultado::new)
        public ConteudoResultado(DiagnosticoConteudo c) {
            this(
                    // Código, nome e disciplina do conteúdo
                    c.getConteudo().getCodigo(), c.getConteudo().getNome(), c.getConteudo().getDisciplina().getCodigo(),
                    // Acertos, probabilidade e decisão do modelo
                    c.getAcertos(), c.getProbabilidade(), c.getSituacao().getCodigo(),
                    // Decisão do professor, ou null se ainda não confirmou
                    c.getSituacaoProfessor() == null ? null : c.getSituacaoProfessor().getCodigo(),
                    // Decisão que vale
                    c.getSituacaoFinal().getCodigo());
        }
    }

    // Item da lista de reforço: conteúdo, situação, probabilidade e estratégia de estudo
    public record Reforco(
            String conteudo, String nome, String disciplina,
            String situacao, BigDecimal probabilidade, String estrategia) {

        // Construtor que converte um conteúdo em item de reforço (usado como Reforco::new)
        public Reforco(DiagnosticoConteudo c) {
            this(
                    // Código, nome e disciplina do conteúdo
                    c.getConteudo().getCodigo(), c.getConteudo().getNome(), c.getConteudo().getDisciplina().getCodigo(),
                    // Situação final, probabilidade e estratégia de estudo
                    c.getSituacaoFinal().getCodigo(), c.getProbabilidade(), c.getConteudo().getEstrategia());
        }
    }

    // Construtor que converte o diagnóstico gravado no formato de resposta (usado como DiagnosticoResponse::new)
    public DiagnosticoResponse(Diagnostico diagnostico) {
        // Chama o construtor principal do record; as listas são montadas pelos métodos auxiliares abaixo
        this(
                diagnostico.getId(),
                diagnostico.getAluno().getCodigo(),
                diagnostico.getSerie(),
                diagnostico.getDataHora(),
                diagnostico.getVersaoModelo(),
                // Resumo com a quantidade de lacunas
                mensagem(diagnostico),
                // Nível de cada disciplina
                disciplinas(diagnostico),
                // Converte cada resultado de conteúdo, na ordem do modelo
                ordenados(diagnostico).stream().map(ConteudoResultado::new).toList(),
                // Conteúdos a reforçar
                reforcar(diagnostico)
        );
    }

    /** Probabilidade com 6 casas decimais, a mesma precisão gravada no banco. */
    public static BigDecimal arredondar(double probabilidade) {
        // Converte para BigDecimal e arredonda para 6 casas (0,5 para cima)
        return BigDecimal.valueOf(probabilidade).setScale(6, RoundingMode.HALF_UP);
    }

    // Os 8 conteúdos na ordem do modelo (0 a 7)
    private static List<DiagnosticoConteudo> ordenados(Diagnostico diagnostico) {
        return diagnostico.getConteudos().stream()
                // Ordena pela posição do conteúdo no vetor do modelo
                .sorted(Comparator.comparing((DiagnosticoConteudo c) -> c.getConteudo().getOrdemModelo()))
                .toList();
    }

    // Resumo em texto com a quantidade de lacunas (pela decisão final)
    private static String mensagem(Diagnostico diagnostico) {
        // Quantidade de lacunas considerando a decisão final
        long lacunas = diagnostico.getConteudos().stream().filter(c -> c.getSituacaoFinal() == Situacao.LACUNA).count();
        return lacunas + " lacuna(s) encontrada(s).";
    }

    // Níveis das disciplinas, ordenados pelo id (Língua Portuguesa e depois Matemática)
    private static List<DisciplinaResultado> disciplinas(Diagnostico diagnostico) {
        // Conteúdos na ordem do modelo, para as listas de lacunas e dúvidas saírem nessa ordem
        List<DiagnosticoConteudo> conteudos = ordenados(diagnostico);
        return diagnostico.getDisciplinas().stream()
                .sorted(Comparator.comparing((DiagnosticoDisciplina d) -> d.getDisciplina().getId()))
                // Monta o resultado de cada disciplina
                .map(d -> new DisciplinaResultado(d, conteudos))
                .toList();
    }

    // Lacunas primeiro, depois "a confirmar"; em cada grupo, a maior probabilidade primeiro (decisão final)
    private static List<Reforco> reforcar(Diagnostico diagnostico) {
        return ordenados(diagnostico).stream()
                // Descarta os conteúdos sem lacuna
                .filter(c -> c.getSituacaoFinal() != Situacao.SEM_LACUNA)
                .sorted(Comparator
                        // false (é lacuna) vem antes de true (não é lacuna)
                        .comparing((DiagnosticoConteudo c) -> c.getSituacaoFinal() != Situacao.LACUNA)
                        // Desempate: probabilidade em ordem decrescente
                        .thenComparing(DiagnosticoConteudo::getProbabilidade, Comparator.reverseOrder()))
                // Converte cada conteúdo em um item de reforço
                .map(Reforco::new)
                .toList();
    }

    // Lista os códigos dos conteúdos de uma disciplina que estão em uma situação
    private static List<String> codigos(List<DiagnosticoConteudo> conteudos, DiagnosticoDisciplina d,
                                        Situacao situacao) {
        return conteudos.stream()
                // Mantém os conteúdos na situação pedida (pela decisão final)
                .filter(c -> c.getSituacaoFinal() == situacao)
                // Mantém só os conteúdos da disciplina
                .filter(c -> c.getConteudo().getDisciplina().getId().equals(d.getDisciplina().getId()))
                // Pega o código de cada conteúdo
                .map(c -> c.getConteudo().getCodigo())
                .toList();
    }
}
