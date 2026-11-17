// Pacote do diagnóstico: entidades, regras de decisão, DTOs, repositório e services
package br.com.fiap3espa.api.diagnostico;

/** Nível do aluno em uma disciplina, com a orientação correspondente (modelo_info.json: orientacao_por_nivel). */
public enum NivelDisciplina {
    // Nenhuma lacuna na disciplina
    ADEQUADO("adequado",
            "Manter a rotina de estudo e propor desafios um pouco acima da série."),
    // 1 ou 2 lacunas na disciplina
    EM_DESENVOLVIMENTO("em desenvolvimento",
            "Reforçar os conteúdos indicados com 15 a 20 minutos de prática, 3 vezes por semana, "
                    + "e rever o progresso em 1 mês."),
    // 3 ou 4 lacunas na disciplina
    ABAIXO_DO_ESPERADO("abaixo do esperado",
            "Priorizar os 2 conteúdos mais urgentes por vez, com acompanhamento semanal do "
                    + "professor e envolvimento da família; refazer o questionário em 1 mês."),
    // Conteúdos duvidosos podem mudar o nível: depende da confirmação do professor
    A_CONFIRMAR("a confirmar",
            "Há conteúdos com resultado duvidoso: o professor deve avaliá-los antes de definir o plano.");

    // Texto do nível exibido na resposta da API (ex.: "em desenvolvimento")
    private final String codigo;
    // Orientação pedagógica para o nível
    private final String orientacao;

    // Construtor de cada constante do enum (chamado uma vez para cada valor acima)
    NivelDisciplina(String codigo, String orientacao) {
        // Guarda o texto do nível
        this.codigo = codigo;
        // Guarda a orientação
        this.orientacao = orientacao;
    }

    /** 0 lacunas = adequado; 1 a 2 = em desenvolvimento; 3 a 4 = abaixo do esperado. */
    public static NivelDisciplina porLacunas(int lacunas) {
        // Sem lacunas: adequado
        if (lacunas == 0) {
            return ADEQUADO;
        }
        // Até 2 lacunas: em desenvolvimento; 3 ou mais: abaixo do esperado
        return lacunas <= 2 ? EM_DESENVOLVIMENTO : ABAIXO_DO_ESPERADO;
    }

    /** "a confirmar" quando os conteúdos duvidosos podem mudar o nível; senão, o nível pelas lacunas. */
    public static NivelDisciplina calcular(int lacunas, int duvidas) {
        // Melhor caso: nenhum conteúdo duvidoso é lacuna
        NivelDisciplina minimo = porLacunas(lacunas);
        // Pior caso: todos os conteúdos duvidosos são lacunas
        NivelDisciplina maximo = porLacunas(lacunas + duvidas);
        // Se os dois casos dão o mesmo nível, as dúvidas não importam; senão, o nível fica "a confirmar"
        return minimo == maximo ? minimo : A_CONFIRMAR;
    }

    /** Mesmo texto usado no contrato do modelo e nos golden tests. */
    public String getCodigo() {
        return codigo;
    }

    // Retorna a orientação pedagógica do nível
    public String getOrientacao() {
        return orientacao;
    }
}
