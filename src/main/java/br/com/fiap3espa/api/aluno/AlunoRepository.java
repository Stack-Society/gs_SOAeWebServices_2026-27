// Pacote da funcionalidade de alunos: entidade, DTOs, repositório e service
package br.com.fiap3espa.api.aluno;

// Paginação do Spring Data:
import org.springframework.data.domain.Page;      // uma página de resultados (conteúdo + totais)
import org.springframework.data.domain.Pageable;  // pedido de página: número, tamanho e ordenação
// Permite carregar relacionamentos LAZY na mesma consulta (evita consultas extras)
import org.springframework.data.jpa.repository.EntityGraph;
// Interface do Spring Data com os métodos prontos de CRUD (save, findById, findAll, delete...)
import org.springframework.data.jpa.repository.JpaRepository;

// Resultado que pode existir ou não (evita null)
import java.util.Optional;

/**
 * Repositório de alunos. O Spring Data gera a implementação sozinho:
 * a consulta SQL é deduzida pelo nome de cada método.
 */
// JpaRepository<Aluno, Long>: entidade Aluno com chave primária do tipo Long
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    /** Uma página de alunos, já com a escola carregada. */
    // Sobrescreve o findAll(Pageable) do JpaRepository só para acrescentar o JOIN com a escola
    @Override
    @EntityGraph(attributePaths = "escola")
    // SELECT ... ORDER BY <ordenação pedida> com OFFSET/FETCH da página
    Page<Aluno> findAll(Pageable paginacao);

    /** Aluno pelo id, já com a escola carregada. */
    @EntityGraph(attributePaths = "escola")
    // "Com Escola" é só um nome descritivo; o Spring Data considera o que vem depois de "By": WHERE id = ?
    Optional<Aluno> findComEscolaById(Long id);

    /** Usado para impedir a exclusão de uma escola que ainda tem alunos. */
    // Retorna true se existir algum aluno com escola_id = ?
    boolean existsByEscolaId(Long escolaId);

    /** Aluno pelo código anônimo, já com a escola carregada. */
    @EntityGraph(attributePaths = "escola")
    // WHERE codigo = ?
    Optional<Aluno> findByCodigo(String codigo);

    /** Usado para recusar código de aluno repetido antes de gravar. */
    // Retorna true se o código já estiver em uso
    boolean existsByCodigo(String codigo);
}
