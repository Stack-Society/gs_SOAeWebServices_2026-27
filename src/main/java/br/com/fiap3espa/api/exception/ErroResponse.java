// Pacote das exceções e do tratamento de erros
package br.com.fiap3espa.api.exception;

// Classes do Java:
import java.time.LocalDateTime;     // data e hora
import java.util.List;              // lista

/** Corpo padrão das respostas de erro da API. */
public record ErroResponse(
        // Código HTTP (400, 404, 409...)
        int status,
        // Resumo do erro
        String mensagem,
        // Lista de problemas encontrados
        List<String> detalhes,
        // Quando o erro ocorreu
        LocalDateTime dataHora) {

    // Construtor que cria o corpo de erro já com a data e hora atuais
    public ErroResponse(int status, String mensagem, List<String> detalhes) {
        // Chama o construtor principal do record, completando a data e hora
        this(status, mensagem, detalhes, LocalDateTime.now());
    }
}
