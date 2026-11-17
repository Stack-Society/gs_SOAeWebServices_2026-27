// Pacote do questionário: catálogo (disciplinas, conteúdos e questões), DTOs, repositórios e service
package br.com.fiap3espa.api.questionario;

// Anotações do JPA usadas para mapear a classe na tabela do banco:
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
 * Conteúdo avaliado pelo questionário (ex.: Decodificação, Operações).
 * São 8 conteúdos fixos, cadastrados pela migração V2; a API apenas os lê.
 */
// Entidade persistida
// name: nome da entidade usado nas consultas JPQL
@Entity(name = "Conteudo")
// Tabela GS2627_1_conteudo, criada pela migração V1
@Table(name = "GS2627_1_conteudo")
// Lombok: construtor vazio exigido pelo JPA
@NoArgsConstructor
// Lombok: construtor com todos os campos
@AllArgsConstructor
// Lombok: gera os getters de todos os campos
@Getter
// Lombok: equals e hashCode comparam só o id (dois objetos com o mesmo id são a mesma entidade)
@EqualsAndHashCode(of = "id")
public class Conteudo {

    // Chave primária
    @Id
    // Gerada pelo Oracle (IDENTITY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código usado na API e no contrato do modelo (ex.: "decodificacao"); único
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    // Nome de exibição (ex.: "Decodificação")
    @Column(nullable = false, length = 60)
    private String nome;

    /** Posição do conteúdo na entrada e na saída do modelo ONNX (0 a 7). */
    // Coluna ordem_modelo: única, porque cada posição do vetor do modelo pertence a um só conteúdo
    @Column(name = "ordem_modelo", nullable = false, unique = true)
    private Integer ordemModelo;

    // Estratégia de estudo recomendada quando o conteúdo é uma lacuna
    @Column(nullable = false, length = 500)
    private String estrategia;

    // Vários conteúdos pertencem a uma disciplina; carregada só quando acessada
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // Coluna disciplina_id, chave estrangeira para GS2627_1_disciplina
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;
}
