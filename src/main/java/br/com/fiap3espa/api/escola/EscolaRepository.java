// Pacote da funcionalidade de escolas: entidade, DTOs, repositório e service
package br.com.fiap3espa.api.escola;

// Paginação do Spring Data:
import org.springframework.data.domain.Page;      // uma página de resultados (conteúdo + totais)
import org.springframework.data.domain.Pageable;  // pedido de página: número, tamanho e ordenação
// Métodos prontos de CRUD (inclusive findAll(Pageable), usado na listagem paginada)
import org.springframework.data.jpa.repository.JpaRepository;

// Resultado que pode existir ou não
import java.util.Optional;

/** Repositório de escolas. */
// Entidade Escola com chave Long
public interface EscolaRepository extends JpaRepository<Escola, Long> {

    // Escola pelo código: WHERE codigo = ?
    Optional<Escola> findByCodigo(String codigo);

    /** Usado para recusar código de escola repetido antes de gravar. */
    // Retorna true se o código já estiver em uso
    boolean existsByCodigo(String codigo);
}
