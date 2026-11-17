// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Entidades do catálogo do questionário
import br.com.fiap3espa.api.questionario.Conteudo;
import br.com.fiap3espa.api.questionario.Questao;
// Exceção para questionário inválido (vira 400)
import br.com.fiap3espa.api.exception.QuestionarioInvalidoException;
// Repositórios do catálogo do questionário
import br.com.fiap3espa.api.questionario.ConteudoRepository;
import br.com.fiap3espa.api.questionario.QuestaoRepository;
// Injeção de dependências automática pelo Spring (@Autowired)
import org.springframework.beans.factory.annotation.Autowired;
// Serviço gerenciado pelo Spring
import org.springframework.stereotype.Service;
// Controle de transação
import org.springframework.transaction.annotation.Transactional;

// Coleções do Java:
import java.util.ArrayList;     // lista mutável
import java.util.HashMap;       // mapa mutável
import java.util.List;          // lista
import java.util.Map;           // mapa
import java.util.Set;           // conjunto (sem repetição)
import java.util.TreeSet;       // conjunto ordenado

/**
 * Validação e correção do questionário (passos 1 e 2 do diagnóstico).
 * Mesma regra das funções validar_respostas e corrigir do notebook.
 */
@Service
public class CorrecaoService {

    // Únicas alternativas aceitas nas respostas
    private static final Set<String> ALTERNATIVAS = Set.of("A", "B", "C");

    // Acesso às questões (gabarito)
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private QuestaoRepository questaoRepository;

    // Acesso aos conteúdos (ordem do modelo)
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private ConteudoRepository conteudoRepository;

    /** Lista os problemas do questionário. Lista vazia = pode ser corrigido. */
    @Transactional(readOnly = true)
    public List<String> validar(Integer serie, Map<String, String> respostas) {
        // Busca o gabarito da série e valida; respostas nulas são tratadas como mapa vazio
        return validar(serie, buscarQuestoes(serie), respostas == null ? Map.of() : respostas);
    }

    /**
     * Conta os acertos de cada conteúdo, na ordem do modelo (0 a 7).
     * Falha fechado: questionário inválido lança QuestionarioInvalidoException e não é corrigido.
     */
    @Transactional(readOnly = true)
    public List<AcertoConteudo> corrigir(Integer serie, Map<String, String> respostas) {
        // Evita NullPointerException se as respostas vierem nulas
        Map<String, String> resp = respostas == null ? Map.of() : respostas;
        // As 24 questões da série, com gabarito e conteúdo
        List<Questao> questoes = buscarQuestoes(serie);

        // Valida antes de corrigir
        List<String> problemas = validar(serie, questoes, resp);
        // Qualquer problema impede a correção (400 com a lista de problemas)
        if (!problemas.isEmpty()) {
            throw new QuestionarioInvalidoException(problemas);
        }

        // Mapa id do conteúdo → quantidade de acertos
        Map<Long, Integer> acertosPorConteudo = new HashMap<>();
        // Percorre as 24 questões
        for (Questao questao : questoes) {
            // Compara o gabarito com a resposta do aluno para a questão (ex.: "q5")
            if (questao.getGabarito().equals(resp.get(chave(questao)))) {
                // Soma 1 acerto ao conteúdo da questão (cria a entrada com 1 se ainda não existir)
                acertosPorConteudo.merge(questao.getConteudo().getId(), 1, Integer::sum);
            }
        }

        // Lista final com os 8 conteúdos
        List<AcertoConteudo> resultado = new ArrayList<>();
        // Percorre os conteúdos na ordem do modelo (0 a 7)
        for (Conteudo conteudo : conteudoRepository.findAllByOrderByOrdemModeloAsc()) {
            // Conteúdo sem nenhum acerto fica com 0
            resultado.add(new AcertoConteudo(conteudo, acertosPorConteudo.getOrDefault(conteudo.getId(), 0)));
        }
        // Devolve os acertos por conteúdo
        return resultado;
    }

    // Validação propriamente dita, usando as questões já buscadas
    private List<String> validar(Integer serie, List<Questao> questoes, Map<String, String> respostas) {
        // Sem série válida não há gabarito: nem dá para conferir as respostas.
        if (questoes.isEmpty()) {
            return List.of("série inválida: " + serie + " (esperado: 1 a 5)");
        }

        // Lista onde os problemas são acumulados
        List<String> problemas = new ArrayList<>();

        // Começa com todas as chaves enviadas (ordenadas, para a mensagem ficar estável)
        Set<String> desconhecidas = new TreeSet<>(respostas.keySet());
        // Retira as chaves válidas (q1 a q24); o que sobrar não existe no questionário
        questoes.forEach(questao -> desconhecidas.remove(chave(questao)));
        // Registra as chaves desconhecidas (ex.: "q25")
        if (!desconhecidas.isEmpty()) {
            problemas.add("questões desconhecidas: " + desconhecidas);
        }

        // Confere cada uma das 24 questões
        for (Questao questao : questoes) {
            // Nome da questão nas respostas (ex.: "q7")
            String q = chave(questao);
            // Alternativa enviada pelo aluno
            String resposta = respostas.get(q);
            // Questão sem resposta ou com texto vazio
            if (resposta == null || resposta.isEmpty()) {
                problemas.add(q + ": sem resposta");
            // Resposta fora de A, B ou C (ex.: "D" ou "a")
            } else if (!ALTERNATIVAS.contains(resposta)) {
                problemas.add(q + ": resposta inválida '" + resposta + "'");
            }
        }
        // Lista vazia significa questionário válido
        return problemas;
    }

    // Busca as questões da série; série nula devolve lista vazia (tratada como série inválida)
    private List<Questao> buscarQuestoes(Integer serie) {
        return serie == null ? List.of() : questaoRepository.findBySerieOrderByNumeroAsc(serie);
    }

    /** Nome da questão nas respostas: q1 a q24. */
    private static String chave(Questao questao) {
        return "q" + questao.getNumero();
    }
}
