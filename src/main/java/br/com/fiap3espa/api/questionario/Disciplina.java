// Pacote do questionário: catálogo (disciplinas, conteúdos e questões), DTOs, repositórios e service
package br.com.fiap3espa.api.questionario;

// Anotações do JPA:
import jakarta.persistence.Column;          // configura uma coluna
import jakarta.persistence.Entity;          // marca a classe como entidade
import jakarta.persistence.GeneratedValue;  // chave gerada automaticamente
import jakarta.persistence.GenerationType;  // estratégia de geração da chave
import jakarta.persistence.Id;              // chave primária
import jakarta.persistence.Table;           // nome da tabela
// Anotações do Lombok (geram código em tempo de compilação):
import lombok.AllArgsConstructor;  // construtor com todos os campos
import lombok.EqualsAndHashCode;   // equals e hashCode
import lombok.Getter;              // getters
import lombok.NoArgsConstructor;   // construtor sem argumentos

/**
 * Disciplina do questionário: Língua Portuguesa ou Matemática.
 * Cadastradas pela migração V2; a API apenas as lê.
 */
// Entidade persistida
// name: nome da entidade usado nas consultas JPQL
@Entity(name = "Disciplina")
// Tabela GS2627_1_disciplina, criada pela migração V1
@Table(name = "GS2627_1_disciplina")
// Lombok: construtor vazio exigido pelo JPA
@NoArgsConstructor
// Lombok: construtor com todos os campos
@AllArgsConstructor
// Lombok: gera os getters de todos os campos
@Getter
// Lombok: equals e hashCode comparam só o id (dois objetos com o mesmo id são a mesma entidade)
@EqualsAndHashCode(of = "id")
public class Disciplina {

    // Chave primária
    @Id
    // Gerada pelo Oracle (IDENTITY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código usado na API (ex.: "portugues"); único
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    // Nome de exibição (ex.: "Língua Portuguesa")
    @Column(nullable = false, length = 60)
    private String nome;
}
