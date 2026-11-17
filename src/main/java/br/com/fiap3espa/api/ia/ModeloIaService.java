// Pacote da integração com o modelo de IA (ONNX)
package br.com.fiap3espa.api.ia;

// Acertos por conteúdo, calculados pela correção do diagnóstico
import br.com.fiap3espa.api.diagnostico.AcertoConteudo;

// Classes do ONNX Runtime:
import ai.onnxruntime.OnnxTensor;       // tensor (matriz de números) de entrada do modelo
import ai.onnxruntime.OrtEnvironment;   // ambiente global do ONNX Runtime
import ai.onnxruntime.OrtException;     // erro do ONNX Runtime
import ai.onnxruntime.OrtSession;       // sessão com o modelo carregado, pronta para executar
// Executa um método quando a aplicação está sendo encerrada
import jakarta.annotation.PreDestroy;
// Lê arquivos de dentro de src/main/resources (classpath)
import org.springframework.core.io.ClassPathResource;
// Serviço gerenciado pelo Spring
import org.springframework.stereotype.Service;

// Classes do Java:
import java.io.IOException;     // erro de leitura de arquivo
import java.io.InputStream;     // fluxo de leitura de bytes
import java.util.List;          // lista
import java.util.Map;           // mapa
import java.util.Set;           // conjunto

/**
 * Executa o modelo de IA (modelo.onnx) dentro da API, sem Python.
 * Entrada "acertos": float32 [1, 13] = 8 acertos (ordem do modelo) + série em one-hot (serie_1..serie_5).
 * Saída "probabilities": float32 [1, 8] = probabilidade de lacuna em cada conteúdo, na mesma ordem.
 */
@Service
public class ModeloIaService {

    // Caminho do modelo dentro de src/main/resources
    private static final String CAMINHO_MODELO = "model/modelo.onnx";
    // Nome da entrada do modelo
    private static final String ENTRADA = "acertos";
    // Nome da saída usada
    private static final String SAIDA = "probabilities"; // a saída "label" usa limiar 0,5 e não é usada
    // Quantidade de conteúdos (8 primeiras posições da entrada)
    private static final int TOTAL_CONTEUDOS = 8;
    // Quantidade de séries (5 últimas posições da entrada, em one-hot)
    private static final int TOTAL_SERIES = 5;

    // Ambiente do ONNX Runtime
    private final OrtEnvironment ambiente;
    // Sessão com o modelo carregado (criada uma vez e reutilizada em todas as requisições)
    private final OrtSession sessao;

    // Carrega o modelo quando a aplicação sobe; se falhar, a aplicação não inicia
    public ModeloIaService() throws IOException, OrtException {
        // Obtém o ambiente global do ONNX Runtime
        this.ambiente = OrtEnvironment.getEnvironment();
        // Abre o arquivo do modelo; o try-with-resources fecha o fluxo no final
        try (InputStream modelo = new ClassPathResource(CAMINHO_MODELO).getInputStream()) {
            // Lê todos os bytes do arquivo e cria a sessão com as opções padrão
            this.sessao = ambiente.createSession(modelo.readAllBytes(), new OrtSession.SessionOptions());
        }
    }

    /** Probabilidade de lacuna em cada conteúdo, na ordem do modelo (0 a 7). */
    public float[] preverProbabilidades(int serie, List<AcertoConteudo> acertos) {
        // Monta o vetor de entrada e executa o modelo
        return executar(montarFeatures(serie, acertos));
    }

    /** Vetor de entrada do modelo: 8 acertos na ordem do modelo + série em one-hot. */
    static float[] montarFeatures(int serie, List<AcertoConteudo> acertos) {
        // O modelo exige exatamente 8 conteúdos
        if (acertos.size() != TOTAL_CONTEUDOS) {
            throw new IllegalArgumentException("esperados 8 conteúdos, recebidos " + acertos.size());
        }
        // A série precisa estar entre 1 e 5
        if (serie < 1 || serie > TOTAL_SERIES) {
            throw new IllegalArgumentException("série inválida: " + serie);
        }

        // Vetor de 13 posições, todas começando em 0
        float[] features = new float[TOTAL_CONTEUDOS + TOTAL_SERIES];
        // Preenche as posições 0 a 7 com os acertos
        for (AcertoConteudo acerto : acertos) {
            // Posição do conteúdo no vetor
            Integer posicao = acerto.conteudo().getOrdemModelo();
            // A posição precisa existir e estar entre 0 e 7
            if (posicao == null || posicao < 0 || posicao >= TOTAL_CONTEUDOS) {
                throw new IllegalArgumentException("ordem do modelo inválida para o conteúdo "
                        + acerto.conteudo().getCodigo() + ": " + posicao);
            }
            // Acertos (0 a 3) convertidos para float
            features[posicao] = acerto.acertos();
        }
        // One-hot da série: marca 1 na posição 8 (1º ano) até 12 (5º ano)
        features[TOTAL_CONTEUDOS + serie - 1] = 1.0f;
        // Devolve o vetor pronto
        return features;
    }

    // Executa o modelo com um vetor de 13 posições e devolve as 8 probabilidades
    float[] executar(float[] features) {
        // Cria o tensor [1, 13] (uma linha) e executa pedindo só a saída "probabilities"; ambos são fechados no final
        try (OnnxTensor entrada = OnnxTensor.createTensor(ambiente, new float[][]{features});
             OrtSession.Result resultado = sessao.run(Map.of(ENTRADA, entrada), Set.of(SAIDA))) {
            // Lê a saída como matriz [1, 8]
            float[][] probabilidades = (float[][]) resultado.get(SAIDA).orElseThrow().getValue();
            // Devolve a primeira (e única) linha
            return probabilidades[0];
        // Erro na execução do modelo vira erro interno (500)
        } catch (OrtException e) {
            throw new IllegalStateException("falha ao executar o modelo de IA", e);
        }
    }

    // Chamado pelo Spring ao encerrar a aplicação
    @PreDestroy
    void fechar() throws OrtException {
        // Libera a memória nativa usada pelo modelo
        sessao.close();
    }
}
