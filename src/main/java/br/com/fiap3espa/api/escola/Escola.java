// Pacote da funcionalidade de escolas: entidade, DTOs, repositório e service
package br.com.fiap3espa.api.escola;

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

/** Escola à qual os alunos pertencem. */
// Entidade persistida
// name: nome da entidade usado nas consultas JPQL
@Entity(name = "Escola")
// Tabela GS2627_1_escola, criada pela migração V4
@Table(name = "GS2627_1_escola")
// Lombok: construtor vazio exigido pelo JPA
@NoArgsConstructor
// Lombok: construtor com todos os campos
@AllArgsConstructor
// Lombok: gera os getters de todos os campos
@Getter
// Lombok: equals e hashCode comparam só o id (dois objetos com o mesmo id são a mesma entidade)
@EqualsAndHashCode(of = "id")
public class Escola {

    // Chave primária
    @Id
    // Gerada pelo Oracle (IDENTITY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código que identifica a escola; obrigatório, único e com até 20 caracteres
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    // Nome da escola
    @Column(nullable = false, length = 100)
    private String nome;

    // Construtor usado no cadastro de uma nova escola
    public Escola(String codigo, String nome) {
        // Guarda o código
        this.codigo = codigo;
        // Guarda o nome
        this.nome = nome;
    }

    /** O código não muda: identifica a escola. */
    public void atualizar(String nome) {
        // Só o nome pode ser alterado
        this.nome = nome;
    }
}
