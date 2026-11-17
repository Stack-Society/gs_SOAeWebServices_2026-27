// Pacote do questionário: catálogo (disciplinas, conteúdos e questões), DTOs, repositórios e service
package br.com.fiap3espa.api.questionario;

// Injeção de dependências automática pelo Spring (@Autowired)
import org.springframework.beans.factory.annotation.Autowired;
// Serviço gerenciado pelo Spring
import org.springframework.stereotype.Service;
// Controle de transação
import org.springframework.transaction.annotation.Transactional;

// Lista
import java.util.List;

/** Monta o questionário de uma série (sem o gabarito). */
@Service
public class QuestionarioService {

    // Acesso às questões
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private QuestaoRepository questaoRepository;

    // Busca as 24 questões da série
    @Transactional(readOnly = true)
    public QuestionarioResponse buscarPorSerie(Integer serie) {
        // Busca em ordem de número e converte cada questão em QuestaoResponse
        List<QuestaoResponse> questoes = questaoRepository.findBySerieOrderByNumeroAsc(serie)
                .stream()
                .map(QuestaoResponse::new)
                .toList();

        // Monta a resposta com a série, o total e as questões
        return new QuestionarioResponse(serie, questoes.size(), questoes);
    }
}
