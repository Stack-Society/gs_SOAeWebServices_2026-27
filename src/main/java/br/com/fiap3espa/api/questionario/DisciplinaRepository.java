// Pacote do questionário: catálogo (disciplinas, conteúdos e questões), DTOs, repositórios e service
package br.com.fiap3espa.api.questionario;

// Métodos prontos de CRUD
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório das disciplinas: usa só os métodos herdados do JpaRepository. */
// Entidade Disciplina com chave Long
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
}
