// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Entidade da disciplina avaliada (catálogo do questionário)
import br.com.fiap3espa.api.questionario.Disciplina;

// Anotações do JPA:
import jakarta.persistence.Column;          // configura uma coluna
import jakarta.persistence.Entity;          // marca a classe como entidade
import jakarta.persistence.EnumType;        // forma de gravar um enum
import jakarta.persistence.Enumerated;      // indica que o campo é um enum
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

/** Nível do aluno em uma disciplina dentro de um diagnóstico. */
// Entidade persistida
// name: nome da entidade usado nas consultas JPQL
@Entity(name = "DiagnosticoDisciplina")
// Tabela GS2627_1_nivel_disciplina, criada pela migração V5
@Table(name = "GS2627_1_nivel_disciplina")
// Lombok: construtor vazio exigido pelo JPA
@NoArgsConstructor
// Lombok: construtor com todos os campos
@AllArgsConstructor
// Lombok: gera os getters de todos os campos
@Getter
// Lombok: equals e hashCode comparam só o id (dois objetos com o mesmo id são a mesma entidade)
@EqualsAndHashCode(of = "id")
public class DiagnosticoDisciplina {

    // Chave primária
    @Id
    // Gerada pelo Oracle (IDENTITY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Diagnóstico ao qual este nível pertence
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // Coluna diagnostico_id, chave estrangeira para GS2627_1_diagnostico
    @JoinColumn(name = "diagnostico_id", nullable = false)
    private Diagnostico diagnostico;

    // Disciplina avaliada (Língua Portuguesa ou Matemática)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // Coluna disciplina_id, chave estrangeira para GS2627_1_disciplina
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    // Nível calculado, gravado pelo nome do enum (ADEQUADO, EM_DESENVOLVIMENTO...)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelDisciplina nivel;

    // Visível só no pacote: o nível só é criado pelo Diagnostico
    DiagnosticoDisciplina(Diagnostico diagnostico, Disciplina disciplina, NivelDisciplina nivel) {
        // Diagnóstico dono do nível
        this.diagnostico = diagnostico;
        // Disciplina avaliada
        this.disciplina = disciplina;
        // Nível calculado
        this.nivel = nivel;
    }

    /** Recalculado quando o professor confirma um conteúdo da disciplina. */
    void atualizarNivel(NivelDisciplina nivel) {
        // Substitui o nível anterior pelo recalculado
        this.nivel = nivel;
    }
}
