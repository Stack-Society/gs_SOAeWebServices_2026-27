// Pacote das exceções e do tratamento de erros
package br.com.fiap3espa.api.exception;

// Lista
import java.util.List;

/** Questionário recusado pela validação: não pode ser corrigido nem receber diagnóstico. */
// Exceção não verificada; tratada no ApiExceptionHandler como 400
public class QuestionarioInvalidoException extends RuntimeException {

    // Todos os problemas encontrados nas respostas (devolvidos em "detalhes")
    private final List<String> problemas;

    // Recebe a lista de problemas encontrados pela validação
    public QuestionarioInvalidoException(List<String> problemas) {
        // Mensagem da exceção: os problemas separados por ponto e vírgula
        super("Questionário inválido: " + String.join("; ", problemas));
        // Guarda uma cópia imutável da lista
        this.problemas = List.copyOf(problemas);
    }

    // Retorna os problemas
    public List<String> getProblemas() {
        return problemas;
    }
}
