// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Métodos prontos de CRUD
import org.springframework.data.jpa.repository.JpaRepository;

// Lista de resultados
import java.util.List;
// Resultado que pode existir ou não
import java.util.Optional;

/** Repositório de diagnósticos (as respostas e os resultados são salvos junto, pelo cascade). */
// Entidade Diagnostico com chave Long
public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Long> {

    /** Histórico de diagnósticos de um aluno, do mais recente para o mais antigo. */
    // WHERE aluno_id = ? ORDER BY data_hora DESC
    List<Diagnostico> findByAlunoIdOrderByDataHoraDesc(Long alunoId);

    /** Diagnóstico pelo id, só se for do aluno informado na URL. */
    // WHERE id = ? AND aluno_id = ? (impede ver o diagnóstico de outro aluno)
    Optional<Diagnostico> findByIdAndAlunoId(Long id, Long alunoId);

    /** Usado para impedir a exclusão de um aluno que já tem diagnósticos. */
    // Retorna true se o aluno tiver pelo menos um diagnóstico
    boolean existsByAlunoId(Long alunoId);
}
