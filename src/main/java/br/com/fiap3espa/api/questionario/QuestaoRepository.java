// Pacote do questionário: catálogo (disciplinas, conteúdos e questões), DTOs, repositórios e service
package br.com.fiap3espa.api.questionario;

// Carrega relacionamentos LAZY na mesma consulta
import org.springframework.data.jpa.repository.EntityGraph;
// Métodos prontos de CRUD
import org.springframework.data.jpa.repository.JpaRepository;

// Lista de resultados
import java.util.List;

/** Repositório das 120 questões (24 por série). */
// Entidade Questao com chave Long
public interface QuestaoRepository extends JpaRepository<Questao, Long> {

    /** As 24 questões de uma série, em ordem, já com o conteúdo e a disciplina carregados. */
    // JOIN com o conteúdo e com a disciplina do conteúdo, tudo em uma consulta
    @EntityGraph(attributePaths = {"conteudo", "conteudo.disciplina"})
    // WHERE serie = ? ORDER BY numero ASC
    List<Questao> findBySerieOrderByNumeroAsc(Integer serie);
}
