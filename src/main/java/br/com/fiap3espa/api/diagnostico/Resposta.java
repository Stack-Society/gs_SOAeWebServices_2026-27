// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

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

/** Alternativa marcada pelo aluno em uma questão (1 a 24). */
// Entidade persistida
// name: nome da entidade usado nas consultas JPQL
@Entity(name = "Resposta")
// Tabela GS2627_1_resposta, criada pela migração V5
@Table(name = "GS2627_1_resposta")
// Lombok: construtor vazio exigido pelo JPA
@NoArgsConstructor
// Lombok: construtor com todos os campos
@AllArgsConstructor
// Lombok: gera os getters de todos os campos
@Getter
// Lombok: equals e hashCode comparam só o id (dois objetos com o mesmo id são a mesma entidade)
@EqualsAndHashCode(of = "id")
public class Resposta {

    // Chave primária
    @Id
    // Gerada pelo Oracle (IDENTITY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Diagnóstico ao qual a resposta pertence
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // Coluna diagnostico_id, chave estrangeira para GS2627_1_diagnostico
    @JoinColumn(name = "diagnostico_id", nullable = false)
    private Diagnostico diagnostico;

    // Número da questão (1 a 24)
    @Column(nullable = false)
    private Integer numero;

    // Alternativa marcada (A, B ou C), em uma coluna CHAR(1)
    @Column(nullable = false, columnDefinition = "CHAR(1)")
    private String alternativa;

    // Visível só no pacote: a resposta só é criada pelo Diagnostico
    Resposta(Diagnostico diagnostico, Integer numero, String alternativa) {
        // Diagnóstico dono da resposta
        this.diagnostico = diagnostico;
        // Número da questão
        this.numero = numero;
        // Alternativa marcada
        this.alternativa = alternativa;
    }
}
