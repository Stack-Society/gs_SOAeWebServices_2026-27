// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

/** Decisão sobre um conteúdo, a partir da probabilidade de lacuna do modelo. */
public enum Situacao {
    // Probabilidade claramente acima do limiar: o aluno tem lacuna no conteúdo
    LACUNA("lacuna"),
    // Probabilidade claramente abaixo do limiar: sem lacuna
    SEM_LACUNA("sem_lacuna"),
    // Probabilidade muito perto do limiar: o professor precisa confirmar
    A_CONFIRMAR("a_confirmar");

    // Texto usado no JSON da API (minúsculo, com sublinhado)
    private final String codigo;

    // Construtor de cada constante do enum
    Situacao(String codigo) {
        // Guarda o texto
        this.codigo = codigo;
    }

    /** Mesmo texto usado no contrato do modelo e nos golden tests. */
    public String getCodigo() {
        return codigo;
    }

    /** "lacuna" → LACUNA, "sem_lacuna" → SEM_LACUNA, "a_confirmar" → A_CONFIRMAR. */
    public static Situacao deCodigo(String codigo) {
        // Percorre todos os valores do enum
        for (Situacao situacao : values()) {
            // Devolve o valor cujo texto é igual ao recebido
            if (situacao.codigo.equals(codigo)) {
                return situacao;
            }
        }
        // Nenhum valor corresponde: texto inválido
        throw new IllegalArgumentException("situação inválida: " + codigo);
    }
}
