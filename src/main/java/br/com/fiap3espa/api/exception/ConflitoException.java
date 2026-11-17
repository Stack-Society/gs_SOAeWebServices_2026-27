// Pacote das exceções e do tratamento de erros
package br.com.fiap3espa.api.exception;

/** Operação conflita com dados existentes (código repetido, registro em uso): vira 409 Conflict. */
// RuntimeException: não precisa ser declarada nos métodos (exceção não verificada)
public class ConflitoException extends RuntimeException {

    // Recebe a mensagem que será devolvida no corpo do erro
    public ConflitoException(String mensagem) {
        // Repassa a mensagem para a classe mãe
        super(mensagem);
    }
}
