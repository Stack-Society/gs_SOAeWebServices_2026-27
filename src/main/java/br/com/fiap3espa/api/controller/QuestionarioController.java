// Pacote dos controllers (endpoints REST)
package br.com.fiap3espa.api.controller;

// DTO de saída
import br.com.fiap3espa.api.questionario.QuestionarioResponse;
// Serviço do questionário
import br.com.fiap3espa.api.questionario.QuestionarioService;
// Validações do parâmetro da URL
import jakarta.validation.constraints.Max;  // valor máximo
import jakarta.validation.constraints.Min;  // valor mínimo
// Injeção de dependências automática pelo Spring (@Autowired)
import org.springframework.beans.factory.annotation.Autowired;
// Anotações do Spring MVC:
import org.springframework.web.bind.annotation.GetMapping;      // requisições GET
import org.springframework.web.bind.annotation.PathVariable;    // valor vindo da URL
import org.springframework.web.bind.annotation.RequestMapping;  // prefixo de URL
import org.springframework.web.bind.annotation.RestController;  // controller REST

/** Endpoint que entrega o questionário de cada série. */
// Controller REST
@RestController
// Todas as rotas começam com /questionarios
@RequestMapping("/questionarios")
public class QuestionarioController {

    // Serviço que monta o questionário
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private QuestionarioService questionarioService;

    /** Questionário da série (1º ao 5º ano). Série fora de 1 a 5 responde 400 Bad Request. */
    // GET /questionarios/{serie}; @Min e @Max validam o número da série na URL
    @GetMapping("/{serie}")
    public QuestionarioResponse buscarPorSerie(@PathVariable @Min(1) @Max(5) Integer serie) {
        return questionarioService.buscarPorSerie(serie);
    }
}
