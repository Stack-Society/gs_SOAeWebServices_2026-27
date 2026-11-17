// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Entidade do conteúdo avaliado (catálogo do questionário)
import br.com.fiap3espa.api.questionario.Conteudo;

// Anotações do JPA:
import jakarta.persistence.Column;          // configura uma coluna
import jakarta.persistence.Entity;          // marca a classe como entidade
import jakarta.persistence.EnumType;        // forma de gravar um enum (STRING = pelo nome)
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

// Classes do Java:
import java.math.BigDecimal;        // número decimal exato
import java.time.LocalDateTime;     // data e hora

/** Resultado de um conteúdo dentro de um diagnóstico: acertos, probabilidade de lacuna e decisão. */
// Entidade persistida
// name: nome da entidade usado nas consultas JPQL
@Entity(name = "DiagnosticoConteudo")
// Tabela GS2627_1_resultado_conteudo (migração V5, com as colunas de confirmação da V6)
@Table(name = "GS2627_1_resultado_conteudo")
// Lombok: construtor vazio exigido pelo JPA
@NoArgsConstructor
// Lombok: construtor com todos os campos
@AllArgsConstructor
// Lombok: gera os getters de todos os campos
@Getter
// Lombok: equals e hashCode comparam só o id (dois objetos com o mesmo id são a mesma entidade)
@EqualsAndHashCode(of = "id")
public class DiagnosticoConteudo {

    // Chave primária
    @Id
    // Gerada pelo Oracle (IDENTITY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Diagnóstico ao qual este resultado pertence
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // Coluna diagnostico_id, chave estrangeira para GS2627_1_diagnostico
    @JoinColumn(name = "diagnostico_id", nullable = false)
    private Diagnostico diagnostico;

    // Conteúdo avaliado (ex.: Inferência)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // Coluna conteudo_id, chave estrangeira para GS2627_1_conteudo
    @JoinColumn(name = "conteudo_id", nullable = false)
    private Conteudo conteudo;

    // Quantidade de acertos nas 3 questões do conteúdo (0 a 3)
    @Column(nullable = false)
    private Integer acertos;

    // Probabilidade de lacuna estimada pelo modelo: NUMBER(7,6), ou seja, 0,000000 a 1,000000
    @Column(nullable = false, precision = 7, scale = 6)
    private BigDecimal probabilidade;

    /** Decisão do modelo de IA: nunca muda depois de gravada. */
    // Gravada pelo nome do enum (LACUNA, SEM_LACUNA, A_CONFIRMAR)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Situacao situacao;

    /** Decisão do professor para um conteúdo "a confirmar" (LACUNA ou SEM_LACUNA); nula enquanto não confirmar. */
    // Gravada pelo nome do enum; coluna opcional
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao_professor", length = 20)
    private Situacao situacaoProfessor;

    // Momento em que o professor confirmou; nulo enquanto não confirmar
    @Column(name = "data_confirmacao")
    private LocalDateTime dataConfirmacao;

    // Construtor sem modificador (visível só no pacote): o resultado só é criado pelo Diagnostico
    DiagnosticoConteudo(Diagnostico diagnostico, Conteudo conteudo, Integer acertos,
                        BigDecimal probabilidade, Situacao situacao) {
        // Diagnóstico dono do resultado
        this.diagnostico = diagnostico;
        // Conteúdo avaliado
        this.conteudo = conteudo;
        // Acertos no conteúdo
        this.acertos = acertos;
        // Probabilidade de lacuna (já arredondada para 6 casas)
        this.probabilidade = probabilidade;
        // Decisão do modelo
        this.situacao = situacao;
    }

    /** A decisão que vale: a do professor, se houver; senão, a do modelo. */
    public Situacao getSituacaoFinal() {
        // Operador ternário: se o professor já decidiu, usa a decisão dele; senão, a do modelo
        return situacaoProfessor != null ? situacaoProfessor : situacao;
    }

    /** Só conteúdos que o modelo deixou "a confirmar" podem ser confirmados (o Diagnostico confere antes). */
    void confirmar(Situacao situacaoProfessor) {
        // Guarda a decisão do professor (a do modelo continua intacta, para auditoria)
        this.situacaoProfessor = situacaoProfessor;
        // Registra quando a confirmação aconteceu
        this.dataConfirmacao = LocalDateTime.now();
    }
}
