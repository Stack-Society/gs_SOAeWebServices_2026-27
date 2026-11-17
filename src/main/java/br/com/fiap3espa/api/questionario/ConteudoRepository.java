// Pacote do questionário: catálogo (disciplinas, conteúdos e questões), DTOs, repositórios e service
package br.com.fiap3espa.api.questionario;

// Carrega relacionamentos LAZY na mesma consulta
import org.springframework.data.jpa.repository.EntityGraph;
// Métodos prontos de CRUD
import org.springframework.data.jpa.repository.JpaRepository;

// Lista de resultados
import java.util.List;

/** Repositório dos 8 conteúdos avaliados (somente leitura na prática). */
// Entidade Conteudo com chave Long
public interface ConteudoRepository extends JpaRepository<Conteudo, Long> {

    /** Os 8 conteúdos na ordem do modelo ONNX, já com a disciplina carregada. */
    // Faz o JOIN com a disciplina na mesma consulta
    @EntityGraph(attributePaths = "disciplina")
    // SELECT ... ORDER BY ordem_modelo ASC
    List<Conteudo> findAllByOrderByOrdemModeloAsc();
}
