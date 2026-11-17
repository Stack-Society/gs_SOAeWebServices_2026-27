// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Classes do Java:
import java.time.LocalDateTime;     // data e hora
import java.util.Comparator;        // critério de ordenação
import java.util.List;              // lista

/** Linha do histórico do aluno: quando foi, níveis e quantos conteúdos ficaram em lacuna ou a confirmar. */
public record DiagnosticoResumoResponse(
        // Id do diagnóstico (usado para buscar o detalhe)
        Long id,
        // Data e hora
        LocalDateTime dataHora,
        // Série
        Integer serie,
        // Versão do modelo
        String versaoModelo,
        // Quantidade de lacunas
        long lacunas,
        // Quantidade de conteúdos a confirmar
        long aConfirmar,
        // Nível de cada disciplina
        List<NivelResumo> niveis
) {

    // Par disciplina → nível
    public record NivelResumo(String disciplina, String nivel) {

        // Construtor que converte o nível gravado de uma disciplina (usado como NivelResumo::new)
        public NivelResumo(DiagnosticoDisciplina d) {
            this(d.getDisciplina().getCodigo(), d.getNivel().getCodigo());
        }
    }

    // Construtor que converte um diagnóstico em linha de histórico (usado como DiagnosticoResumoResponse::new)
    public DiagnosticoResumoResponse(Diagnostico diagnostico) {
        // Chama o construtor principal do record
        this(
                diagnostico.getId(),
                diagnostico.getDataHora(),
                diagnostico.getSerie(),
                diagnostico.getVersaoModelo(),
                // Conta as lacunas
                contar(diagnostico, Situacao.LACUNA),
                // Conta os conteúdos a confirmar
                contar(diagnostico, Situacao.A_CONFIRMAR),
                diagnostico.getDisciplinas().stream()
                        // Ordena as disciplinas pelo id
                        .sorted(Comparator.comparing((DiagnosticoDisciplina d) -> d.getDisciplina().getId()))
                        // Converte cada nível em NivelResumo
                        .map(NivelResumo::new)
                        .toList()
        );
    }

    // Conta os conteúdos do diagnóstico em uma situação (pela decisão final)
    private static long contar(Diagnostico diagnostico, Situacao situacao) {
        return diagnostico.getConteudos().stream().filter(c -> c.getSituacaoFinal() == situacao).count();
    }
}
