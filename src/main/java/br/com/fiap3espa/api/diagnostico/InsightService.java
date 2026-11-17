// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Entidades do catálogo do questionário
import br.com.fiap3espa.api.questionario.Conteudo;
import br.com.fiap3espa.api.questionario.Disciplina;
// Lê valores do application.properties
import org.springframework.beans.factory.annotation.Value;
// Serviço gerenciado pelo Spring
import org.springframework.stereotype.Service;

// Classes do Java:
import java.util.ArrayList;         // lista mutável
import java.util.Comparator;        // critérios de ordenação
import java.util.LinkedHashMap;     // mapa que mantém a ordem de inserção
import java.util.List;              // lista
import java.util.Map;               // mapa

/**
 * Decisão de cada conteúdo (guardrail de saída) e insights do diagnóstico.
 * Mesma regra das funções situacao, nivel_por_lacunas e diagnosticar do notebook.
 */
@Service
public class InsightService {

    // Limiar de lacuna (0.40 no application.properties)
    // @Value: o Spring preenche o campo com a propriedade do application.properties
    @Value("${aprendia.modelo.limiar-lacuna}")
    private double limiarLacuna;

    // Margem de dúvida em torno do limiar (0.05)
    @Value("${aprendia.modelo.margem-duvida}")
    private double margemDuvida;

    /** a_confirmar se |p - limiar| < margem; senão lacuna se p >= limiar; senão sem_lacuna. */
    public Situacao decidir(double probabilidade) {
        // Probabilidade muito perto do limiar (entre 0,35 e 0,45): o professor decide
        if (Math.abs(probabilidade - limiarLacuna) < margemDuvida) {
            return Situacao.A_CONFIRMAR;
        }
        // Fora da margem: lacuna se p >= 0,40; senão, sem lacuna
        return probabilidade >= limiarLacuna ? Situacao.LACUNA : Situacao.SEM_LACUNA;
    }

    /** Nível "a confirmar" quando os conteúdos duvidosos podem mudar o nível da disciplina. */
    static NivelDisciplina nivel(int lacunas, int duvidas) {
        // Usa a mesma regra do domínio (também usada na confirmação do professor)
        return NivelDisciplina.calcular(lacunas, duvidas);
    }

    /** Junta acertos e probabilidades (ordem do modelo) e gera situação, níveis e conteúdos a reforçar. */
    public Insights gerar(List<AcertoConteudo> acertos, float[] probabilidades) {
        // Resultado de cada conteúdo
        List<ResultadoConteudo> conteudos = acertos.stream()
                // Garante a ordem do modelo (0 a 7)
                .sorted(Comparator.comparing(acerto -> acerto.conteudo().getOrdemModelo()))
                .map(acerto -> {
                    // Probabilidade do conteúdo: a posição no vetor é a ordem do modelo
                    double p = probabilidades[acerto.conteudo().getOrdemModelo()];
                    // Junta conteúdo, acertos, probabilidade e decisão
                    return new ResultadoConteudo(acerto.conteudo(), acerto.acertos(), p, decidir(p));
                })
                .toList();

        // Monta os insights: conteúdos, níveis por disciplina e lista de reforço
        return new Insights(conteudos, porDisciplina(conteudos), aReforcar(conteudos));
    }

    // Calcula o nível de cada disciplina
    private List<ResultadoDisciplina> porDisciplina(List<ResultadoConteudo> conteudos) {
        // Agrupa os resultados por id da disciplina, mantendo a ordem em que aparecem
        Map<Long, List<ResultadoConteudo>> grupos = new LinkedHashMap<>();
        for (ResultadoConteudo resultado : conteudos) {
            // Cria a lista do grupo se ainda não existir e acrescenta o resultado
            grupos.computeIfAbsent(resultado.conteudo().getDisciplina().getId(), id -> new ArrayList<>()).add(resultado);
        }

        // Lista final de resultados por disciplina
        List<ResultadoDisciplina> disciplinas = new ArrayList<>();
        // Percorre cada grupo (uma disciplina por vez)
        for (List<ResultadoConteudo> grupo : grupos.values()) {
            // A disciplina do grupo (todos os itens são da mesma)
            Disciplina disciplina = grupo.getFirst().conteudo().getDisciplina();
            // Conteúdos com lacuna
            List<Conteudo> lacunas = filtrar(grupo, Situacao.LACUNA);
            // Conteúdos a confirmar
            List<Conteudo> aConfirmar = filtrar(grupo, Situacao.A_CONFIRMAR);
            // Calcula o nível e guarda o resultado da disciplina
            disciplinas.add(new ResultadoDisciplina(
                    disciplina, nivel(lacunas.size(), aConfirmar.size()), lacunas, aConfirmar));
        }
        // Devolve os níveis
        return disciplinas;
    }

    /** Lacunas primeiro, depois "a confirmar"; em cada grupo, a maior probabilidade primeiro. */
    private static List<ResultadoConteudo> aReforcar(List<ResultadoConteudo> conteudos) {
        return conteudos.stream()
                // Descarta os conteúdos sem lacuna
                .filter(resultado -> resultado.situacao() != Situacao.SEM_LACUNA)
                .sorted(Comparator
                        // false (lacuna) antes de true (a confirmar)
                        .comparing((ResultadoConteudo resultado) -> resultado.situacao() != Situacao.LACUNA)
                        // Desempate: maior probabilidade primeiro
                        .thenComparing(ResultadoConteudo::probabilidade, Comparator.reverseOrder()))
                .toList();
    }

    // Devolve os conteúdos do grupo que estão na situação pedida
    private static List<Conteudo> filtrar(List<ResultadoConteudo> grupo, Situacao situacao) {
        return grupo.stream()
                // Mantém só os resultados na situação
                .filter(resultado -> resultado.situacao() == situacao)
                // Pega o conteúdo de cada resultado
                .map(ResultadoConteudo::conteudo)
                .toList();
    }
}
