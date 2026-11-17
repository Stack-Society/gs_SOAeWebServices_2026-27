// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

// Entidade do aluno dono do diagnóstico
import br.com.fiap3espa.api.aluno.Aluno;
// Entidade do conteúdo avaliado (catálogo do questionário)
import br.com.fiap3espa.api.questionario.Conteudo;
// Entidade da disciplina avaliada (catálogo do questionário)
import br.com.fiap3espa.api.questionario.Disciplina;

// Exceções da própria API (viram 409 e 404 no ApiExceptionHandler)
import br.com.fiap3espa.api.exception.ConflitoException;
import br.com.fiap3espa.api.exception.RecursoNaoEncontradoException;
// Anotações do JPA:
import jakarta.persistence.CascadeType;     // propaga operações (salvar, excluir) para as entidades filhas
import jakarta.persistence.Column;          // configura uma coluna
import jakarta.persistence.Entity;          // marca a classe como entidade
import jakarta.persistence.FetchType;       // define quando um relacionamento é carregado
import jakarta.persistence.GeneratedValue;  // chave gerada automaticamente
import jakarta.persistence.GenerationType;  // estratégia de geração da chave
import jakarta.persistence.Id;              // chave primária
import jakarta.persistence.JoinColumn;      // coluna de chave estrangeira
import jakarta.persistence.ManyToOne;       // relacionamento muitos-para-um
import jakarta.persistence.OneToMany;       // relacionamento um-para-muitos
import jakarta.persistence.OrderBy;         // ordena a lista ao carregar do banco
import jakarta.persistence.Table;           // nome da tabela
// Anotações do Lombok (geram código em tempo de compilação):
import lombok.AllArgsConstructor;  // construtor com todos os campos
import lombok.EqualsAndHashCode;   // equals e hashCode
import lombok.Getter;              // getters
import lombok.NoArgsConstructor;   // construtor sem argumentos

// Classes do Java:
import java.math.BigDecimal;        // número decimal exato (probabilidade com 6 casas)
import java.time.LocalDateTime;     // data e hora sem fuso
import java.util.ArrayList;         // lista mutável
import java.util.Collections;       // utilitário para devolver listas somente leitura
import java.util.List;              // interface de lista

/** Diagnóstico de um questionário válido: respostas, resultado de cada conteúdo e nível de cada disciplina. */
// Entidade persistida
// name: nome da entidade usado nas consultas JPQL
@Entity(name = "Diagnostico")
// Tabela GS2627_1_diagnostico, criada pela migração V5
@Table(name = "GS2627_1_diagnostico")
// Lombok: construtor vazio exigido pelo JPA
@NoArgsConstructor
// Lombok: construtor com todos os campos
@AllArgsConstructor
// Lombok: gera os getters de todos os campos
@Getter
// Lombok: equals e hashCode comparam só o id (dois objetos com o mesmo id são a mesma entidade)
@EqualsAndHashCode(of = "id")
public class Diagnostico {

    // Chave primária
    @Id
    // Gerada pelo Oracle (IDENTITY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Um aluno tem vários diagnósticos (histórico); carregado só quando acessado
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // Coluna aluno_id, chave estrangeira para GS2627_1_aluno
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    /** Série do aluno no momento do questionário (define o gabarito usado). */
    // Guardada aqui porque a série do aluno pode mudar depois
    @Column(nullable = false)
    private Integer serie;

    // Data e hora em que o questionário foi diagnosticado
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    // Versão do modelo de IA usada (permite saber qual modelo gerou cada resultado)
    @Column(name = "versao_modelo", nullable = false, length = 10)
    private String versaoModelo;

    // As 24 respostas; mappedBy: a chave estrangeira fica na entidade Resposta (campo "diagnostico")
    // cascade ALL: salvar o diagnóstico salva as respostas; orphanRemoval: resposta retirada da lista é apagada
    @OneToMany(mappedBy = "diagnostico", cascade = CascadeType.ALL, orphanRemoval = true)
    // Respostas sempre carregadas em ordem de número da questão
    @OrderBy("numero")
    private List<Resposta> respostas = new ArrayList<>();

    // Resultado de cada um dos 8 conteúdos, salvo junto com o diagnóstico
    @OneToMany(mappedBy = "diagnostico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiagnosticoConteudo> conteudos = new ArrayList<>();

    // Nível de cada uma das 2 disciplinas, salvo junto com o diagnóstico
    @OneToMany(mappedBy = "diagnostico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiagnosticoDisciplina> disciplinas = new ArrayList<>();

    // Construtor usado pelo DiagnosticoService ao criar um novo diagnóstico
    public Diagnostico(Aluno aluno, Integer serie, String versaoModelo) {
        // Aluno que respondeu o questionário
        this.aluno = aluno;
        // Série usada para corrigir as respostas
        this.serie = serie;
        // Versão do modelo de IA (vem do application.properties)
        this.versaoModelo = versaoModelo;
        // Momento da criação do diagnóstico
        this.dataHora = LocalDateTime.now();
    }

    // Acrescenta a alternativa marcada em uma questão
    public void adicionarResposta(Integer numero, String alternativa) {
        // Cria a resposta já ligada a este diagnóstico e a coloca na lista
        respostas.add(new Resposta(this, numero, alternativa));
    }

    // Acrescenta o resultado de um conteúdo (acertos, probabilidade do modelo e decisão)
    public void adicionarConteudo(Conteudo conteudo, Integer acertos, BigDecimal probabilidade, Situacao situacao) {
        // Cria o resultado ligado a este diagnóstico e o coloca na lista
        conteudos.add(new DiagnosticoConteudo(this, conteudo, acertos, probabilidade, situacao));
    }

    // Acrescenta o nível calculado para uma disciplina
    public void adicionarDisciplina(Disciplina disciplina, NivelDisciplina nivel) {
        // Cria o nível ligado a este diagnóstico e o coloca na lista
        disciplinas.add(new DiagnosticoDisciplina(this, disciplina, nivel));
    }

    /**
     * Professor decide um conteúdo que o modelo deixou "a confirmar" (LACUNA ou SEM_LACUNA)
     * e o nível da disciplina é recalculado com as decisões finais.
     */
    public void confirmarConteudo(String codigoConteudo, Situacao situacaoProfessor) {
        // O professor precisa tomar uma decisão: "a confirmar" não é uma resposta aceita
        if (situacaoProfessor == Situacao.A_CONFIRMAR) {
            throw new IllegalArgumentException("o professor deve decidir entre lacuna e sem_lacuna");
        }
        // Procura, entre os 8 resultados, o do conteúdo informado na URL
        DiagnosticoConteudo resultado = conteudos.stream()
                // Mantém só o resultado cujo código de conteúdo é igual ao informado
                .filter(c -> c.getConteudo().getCodigo().equals(codigoConteudo))
                // Pega o primeiro (e único) encontrado
                .findFirst()
                // Se não existir, responde 404
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Conteúdo " + codigoConteudo + " não encontrado no diagnóstico " + id));
        // Só conteúdos que o modelo deixou em dúvida podem ser confirmados; os demais respondem 409
        if (resultado.getSituacao() != Situacao.A_CONFIRMAR) {
            throw new ConflitoException("O conteúdo " + codigoConteudo + " já foi decidido pelo modelo ("
                    + resultado.getSituacao().getCodigo() + ") e não precisa de confirmação");
        }
        // Registra a decisão do professor e a data da confirmação
        resultado.confirmar(situacaoProfessor);

        // Id da disciplina do conteúdo confirmado (só o nível dela pode mudar)
        Long disciplinaId = resultado.getConteudo().getDisciplina().getId();
        // Lista os resultados dos conteúdos dessa disciplina
        List<DiagnosticoConteudo> daDisciplina = conteudos.stream()
                .filter(c -> c.getConteudo().getDisciplina().getId().equals(disciplinaId))
                .toList();
        // Conta as lacunas considerando a decisão final (professor, se houver; senão, modelo)
        int lacunas = (int) daDisciplina.stream().filter(c -> c.getSituacaoFinal() == Situacao.LACUNA).count();
        // Conta os conteúdos que ainda continuam "a confirmar"
        int duvidas = (int) daDisciplina.stream().filter(c -> c.getSituacaoFinal() == Situacao.A_CONFIRMAR).count();
        // Atualiza o nível da disciplina com a nova contagem
        disciplinas.stream()
                // Encontra o nível da disciplina afetada
                .filter(d -> d.getDisciplina().getId().equals(disciplinaId))
                // Recalcula com as mesmas regras usadas na criação do diagnóstico
                .forEach(d -> d.atualizarNivel(NivelDisciplina.calcular(lacunas, duvidas)));
    }

    // Os 3 getters das listas são escritos à mão: como já existem, o @Getter do Lombok não os gera,
    // e assim as listas saem somente leitura (alterações só pelos métodos desta classe)

    // Retorna as respostas em uma lista somente leitura
    public List<Resposta> getRespostas() {
        return Collections.unmodifiableList(respostas);
    }

    // Retorna os resultados por conteúdo, somente leitura
    public List<DiagnosticoConteudo> getConteudos() {
        return Collections.unmodifiableList(conteudos);
    }

    // Retorna os níveis por disciplina, somente leitura
    public List<DiagnosticoDisciplina> getDisciplinas() {
        return Collections.unmodifiableList(disciplinas);
    }
}
