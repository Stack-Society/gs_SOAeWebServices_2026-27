// Pacote da funcionalidade de alunos: entidade, DTOs, repositório e service
package br.com.fiap3espa.api.aluno;

// Entidade Escola, do pacote de escolas (relacionamento aluno → escola)
import br.com.fiap3espa.api.escola.Escola;

// Anotações do JPA (Jakarta Persistence) usadas para mapear a classe na tabela do banco:
import jakarta.persistence.Column;          // configura uma coluna (nome, tamanho, obrigatoriedade)
import jakarta.persistence.Entity;          // marca a classe como entidade persistida
import jakarta.persistence.FetchType;       // define quando um relacionamento é carregado (LAZY = só quando usado)
import jakarta.persistence.GeneratedValue;  // indica que o valor da chave é gerado automaticamente
import jakarta.persistence.GenerationType;  // estratégia de geração da chave
import jakarta.persistence.Id;              // marca a chave primária
import jakarta.persistence.JoinColumn;      // define a coluna de chave estrangeira
import jakarta.persistence.ManyToOne;       // relacionamento muitos-para-um
import jakarta.persistence.Table;           // define o nome da tabela
// Anotações do Lombok (geram código em tempo de compilação):
import lombok.AllArgsConstructor;  // construtor com todos os campos
import lombok.EqualsAndHashCode;   // equals e hashCode
import lombok.Getter;              // getters
import lombok.NoArgsConstructor;   // construtor sem argumentos

/** Aluno identificado só por um código anônimo (sem nome, CPF ou outro dado pessoal). */
// Indica ao Hibernate que esta classe representa uma tabela
// name: nome da entidade usado nas consultas JPQL
@Entity(name = "Aluno")
// Tabela GS2627_1_aluno, criada pela migração V4
@Table(name = "GS2627_1_aluno")
// Lombok: construtor vazio exigido pelo JPA
@NoArgsConstructor
// Lombok: construtor com todos os campos
@AllArgsConstructor
// Lombok: gera os getters de todos os campos
@Getter
// Lombok: equals e hashCode comparam só o id (dois objetos com o mesmo id são a mesma entidade)
@EqualsAndHashCode(of = "id")
public class Aluno {

    // Chave primária da tabela
    @Id
    // Valor gerado pelo próprio Oracle (coluna IDENTITY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código anônimo do aluno: obrigatório, único e com até 20 caracteres
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    // Série atual do aluno (1º ao 5º ano); obrigatória
    @Column(nullable = false)
    private Integer serie;

    // Vários alunos pertencem a uma escola; LAZY: a escola só é buscada no banco quando for acessada
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // Coluna escola_id, chave estrangeira para GS2627_1_escola
    @JoinColumn(name = "escola_id", nullable = false)
    private Escola escola;

    // Construtor usado no cadastro de um novo aluno
    public Aluno(String codigo, Integer serie, Escola escola) {
        // Guarda o código anônimo
        this.codigo = codigo;
        // Guarda a série
        this.serie = serie;
        // Guarda a escola à qual o aluno pertence
        this.escola = escola;
    }

    /** O código não muda: identifica o aluno nos diagnósticos. */
    public void atualizar(Integer serie, Escola escola) {
        // Atualiza a série (por exemplo, quando o aluno passa de ano)
        this.serie = serie;
        // Atualiza a escola (por exemplo, em uma transferência)
        this.escola = escola;
    }
}
