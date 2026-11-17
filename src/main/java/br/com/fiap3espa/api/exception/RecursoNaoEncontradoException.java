// Pacote das exceções e do tratamento de erros
package br.com.fiap3espa.api.exception;

/** Registro pedido não existe: vira 404 Not Found. */
// Exceção não verificada; tratada no ApiExceptionHandler
public class RecursoNaoEncontradoException extends RuntimeException {

    // Recebe a mensagem que será devolvida no corpo do erro
    public RecursoNaoEncontradoException(String mensagem) {
        // Repassa a mensagem para a classe mãe
        super(mensagem);
    }
}
