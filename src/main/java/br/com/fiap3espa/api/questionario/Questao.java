// Pacote do questionário: catálogo (disciplinas, conteúdos e questões), DTOs, repositórios e service
package br.com.fiap3espa.api.questionario;

// Anotações do JPA:
import jakarta.persistence.Column;          // configura uma coluna
import jakarta.persistence.Entity;          // marca a classe como entidade
import jakarta.persistence.FetchType;       // define quando um relacionamento é carregado
import jakarta.persistence.GeneratedValue;  // chave gerada automaticamente
import jakarta.persistence.GenerationType;  // estratégia de geração da chave
import jakarta.persistence.Id;              // chave primária
import jakarta.persistence.JoinColumn;      // coluna de chave estrangeira
import jakarta.persistence.ManyToOne;       // relacionamento muitos-para-um
import jakarta.persistence.Table;           // nome da tabela
// Anotações do Lombok (geram código em tempo de compilação):
import lombok.AllArgsConstructor;  // construtor com todos os campos
import lombok.EqualsAndHashCode;   // equals e hashCode
import lombok.Getter;              // getters
import lombok.NoArgsConstructor;   // construtor sem argumentos

/**
 * Questão do questionário de uma série, com o gabarito e o conteúdo que ela avalia.
 * São 24 questões por série (120 no total), cadastradas pela migração V3.
 */
// Entidade persistida
// name: nome da entidade usado nas consultas JPQL
@Entity(name = "Questao")
// Tabela GS2627_1_questao, criada pela migração V1
@Table(name = "GS2627_1_questao")
// Lombok: construtor vazio exigido pelo JPA
@NoArgsConstructor
// Lombok: construtor com todos os campos
@AllArgsConstructor
// Lombok: gera os getters de todos os campos
@Getter
// Lombok: equals e hashCode comparam só o id (dois objetos com o mesmo id são a mesma entidade)
@EqualsAndHashCode(of = "id")
public class Questao {

    // Chave primária
    @Id
    // Gerada pelo Oracle (IDENTITY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Série do questionário (1 a 5)
    @Column(nullable = false)
    private Integer serie;

    // Número da questão dentro do questionário (1 a 24)
    @Column(nullable = false)
    private Integer numero;

    /** Alternativa correta: A, B ou C. */
    // columnDefinition: a coluna é CHAR(1) no Oracle, e o validate do Hibernate precisa saber disso
    @Column(nullable = false, columnDefinition = "CHAR(1)")
    private String gabarito;

    // Conteúdo avaliado pela questão (cada conteúdo tem 3 questões)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // Coluna conteudo_id, chave estrangeira para GS2627_1_conteudo
    @JoinColumn(name = "conteudo_id", nullable = false)
    private Conteudo conteudo;
}
