// Pacote das exceções e do tratamento de erros
package br.com.fiap3espa.api.exception;

// Códigos HTTP (404, 409, 400...)
import org.springframework.http.HttpStatus;
// Resposta HTTP com status e corpo
import org.springframework.http.ResponseEntity;
// Exceção lançada quando o @Valid encontra campos inválidos no JSON
import org.springframework.web.bind.MethodArgumentNotValidException;
// Indica qual exceção cada método trata
import org.springframework.web.bind.annotation.ExceptionHandler;
// Torna a classe um tratador global de exceções para todos os controllers
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Lista
import java.util.List;

/** Converte as exceções da API em respostas HTTP com corpo ErroResponse. */
// Aplica-se a todos os controllers; o retorno de cada método vira o corpo JSON da resposta
@RestControllerAdvice
public class ApiExceptionHandler {

    // Registro não encontrado → 404 Not Found
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException e) {
        // Usa a mensagem da exceção; sem detalhes
        return resposta(HttpStatus.NOT_FOUND, e.getMessage(), List.of());
    }

    // Conflito com dados existentes → 409 Conflict
    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponse> conflito(ConflitoException e) {
        return resposta(HttpStatus.CONFLICT, e.getMessage(), List.of());
    }

    /** Campos do JSON que não passaram nas anotações @NotBlank, @Min, @Max etc. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> camposInvalidos(MethodArgumentNotValidException e) {
        // Monta um detalhe para cada campo com erro
        List<String> detalhes = e.getBindingResult().getFieldErrors().stream()
                // Formato "campo: mensagem" (ex.: "serie: deve ser menor que ou igual à 5")
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                // Ordena para a resposta ser sempre igual
                .sorted()
                .toList();
        // Responde 400 Bad Request
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos", detalhes);
    }

    /** Questionário recusado pela validação (falha fechada): não recebe diagnóstico. */
    @ExceptionHandler(QuestionarioInvalidoException.class)
    public ResponseEntity<ErroResponse> questionarioInvalido(QuestionarioInvalidoException e) {
        // 400 com a lista de problemas encontrados nas respostas
        return resposta(HttpStatus.BAD_REQUEST, "Questionário inválido", e.getProblemas());
    }

    // Monta a ResponseEntity com o status e o corpo ErroResponse
    private static ResponseEntity<ErroResponse> resposta(HttpStatus status, String mensagem, List<String> detalhes) {
        return ResponseEntity.status(status).body(new ErroResponse(status.value(), mensagem, detalhes));
    }
}
