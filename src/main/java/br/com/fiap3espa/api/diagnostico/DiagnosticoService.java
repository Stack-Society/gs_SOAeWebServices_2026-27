// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Service de alunos, usado para buscar o aluno
import br.com.fiap3espa.api.aluno.AlunoService;
// Service que executa o modelo de IA
import br.com.fiap3espa.api.ia.ModeloIaService;

// Entidade do aluno
import br.com.fiap3espa.api.aluno.Aluno;
// Exceção que vira 404
import br.com.fiap3espa.api.exception.RecursoNaoEncontradoException;
// Injeção de dependências automática pelo Spring (@Autowired)
import org.springframework.beans.factory.annotation.Autowired;
// Lê valores do application.properties
import org.springframework.beans.factory.annotation.Value;
// Serviço gerenciado pelo Spring
import org.springframework.stereotype.Service;
// Controle de transação
import org.springframework.transaction.annotation.Transactional;

// Classes do Java:
import java.util.List;  // lista
import java.util.Map;   // mapa

/**
 * Fluxo completo do diagnóstico:
 * validar → corrigir → modelo de IA → decidir → insights → gravar.
 */
@Service
public class DiagnosticoService {

    // Quantidade de questões de cada questionário
    private static final int TOTAL_QUESTOES = 24;

    // Busca o aluno
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private AlunoService alunoService;

    // Valida e corrige as respostas
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private CorrecaoService correcaoService;

    // Executa o modelo de IA
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private ModeloIaService modeloIaService;

    // Decide cada conteúdo e calcula os níveis
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private InsightService insightService;

    // Grava e lê os diagnósticos
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private DiagnosticoRepository diagnosticoRepository;

    // Versão do modelo, gravada em cada diagnóstico
    // @Value: o Spring preenche o campo com a propriedade do application.properties
    @Value("${aprendia.modelo.versao}")
    private String versaoModelo;

    // Executa o diagnóstico completo; tudo em uma transação (ou grava tudo, ou nada)
    @Transactional
    public DiagnosticoResponse diagnosticar(Long alunoId, Map<String, String> respostas) {
        // Busca o aluno (404 se não existir)
        Aluno aluno = alunoService.buscarEntidade(alunoId);
        // A série do cadastro define o gabarito
        int serie = aluno.getSerie();

        // 1 e 2. Valida e corrige; questionário inválido lança QuestionarioInvalidoException (400) e nada é gravado
        List<AcertoConteudo> acertos = correcaoService.corrigir(serie, respostas);

        // 3. Modelo de IA: probabilidade de lacuna em cada conteúdo
        float[] probabilidades = modeloIaService.preverProbabilidades(serie, acertos);

        // 4 e 5. Decisão de cada conteúdo, nível por disciplina e conteúdos a reforçar
        Insights insights = insightService.gerar(acertos, probabilidades);

        // 6. Grava o diagnóstico com as respostas e os resultados
        Diagnostico diagnostico = new Diagnostico(aluno, serie, versaoModelo);
        // Acrescenta as 24 respostas (q1 a q24)
        for (int numero = 1; numero <= TOTAL_QUESTOES; numero++) {
            diagnostico.adicionarResposta(numero, respostas.get("q" + numero));
        }
        // Acrescenta o resultado de cada conteúdo, com a probabilidade arredondada para 6 casas
        for (ResultadoConteudo resultado : insights.conteudos()) {
            diagnostico.adicionarConteudo(resultado.conteudo(), resultado.acertos(),
                    DiagnosticoResponse.arredondar(resultado.probabilidade()), resultado.situacao());
        }
        // Acrescenta o nível de cada disciplina
        for (ResultadoDisciplina resultado : insights.disciplinas()) {
            diagnostico.adicionarDisciplina(resultado.disciplina(), resultado.nivel());
        }

        // Grava tudo de uma vez (cascade) e converte o diagnóstico gravado na resposta
        return new DiagnosticoResponse(diagnosticoRepository.save(diagnostico));
    }

    /** Diagnóstico completo; 404 se o aluno não existir ou o diagnóstico não for dele. */
    @Transactional(readOnly = true)
    public DiagnosticoResponse buscar(Long alunoId, Long diagnosticoId) {
        // Busca pelo id e pelo aluno
        return diagnosticoRepository.findByIdAndAlunoId(diagnosticoId, alunoId)
                // Converte se encontrar
                .map(DiagnosticoResponse::new)
                // Senão, 404
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Diagnóstico " + diagnosticoId + " do aluno " + alunoId + " não encontrado"));
    }

    /**
     * Professor confirma um conteúdo "a confirmar" como lacuna ou sem lacuna; o nível da disciplina é recalculado.
     * 404 se o diagnóstico ou o conteúdo não existir; 409 se o modelo já tiver decidido esse conteúdo.
     */
    @Transactional
    public DiagnosticoResponse confirmar(Long alunoId, Long diagnosticoId, String codigoConteudo,
                                        Situacao situacaoProfessor) {
        // Busca o diagnóstico do aluno (404 se não existir)
        Diagnostico diagnostico = diagnosticoRepository.findByIdAndAlunoId(diagnosticoId, alunoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Diagnóstico " + diagnosticoId + " do aluno " + alunoId + " não encontrado"));
        // A regra fica na entidade; as alterações são gravadas no commit da transação
        diagnostico.confirmarConteudo(codigoConteudo, situacaoProfessor);
        // Devolve o diagnóstico atualizado
        return new DiagnosticoResponse(diagnostico);
    }

    /** Histórico resumido do aluno, do mais recente para o mais antigo; 404 se o aluno não existir. */
    @Transactional(readOnly = true)
    public List<DiagnosticoResumoResponse> historico(Long alunoId) {
        // Só confere se o aluno existe (lança 404 se não existir)
        alunoService.buscarEntidade(alunoId);
        // Busca os diagnósticos do mais recente para o mais antigo e converte cada um em resumo
        return diagnosticoRepository.findByAlunoIdOrderByDataHoraDesc(alunoId).stream()
                .map(DiagnosticoResumoResponse::new)
                .toList();
    }
}
