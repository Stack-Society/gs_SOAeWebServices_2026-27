// Pacote dos controllers (endpoints REST)
package br.com.fiap3espa.api.controller;

// Anotações do Spring MVC:
import org.springframework.web.bind.annotation.GetMapping;      // requisições GET
import org.springframework.web.bind.annotation.RequestMapping;  // prefixo de URL
import org.springframework.web.bind.annotation.RestController;  // controller REST

/** Endpoint simples para conferir se a API está no ar. */
// Controller REST: o retorno do método vai direto no corpo da resposta
@RestController
// Rota /healthcheck
@RequestMapping("/healthcheck")
public class HealthCheckController {

    // GET /healthcheck → 200 com um texto fixo; não acessa o banco nem o modelo
    @GetMapping
    public String healthcheck() {
        // Texto devolvido no corpo da resposta
        return "Teste de integridade da API AprendIA.";
    }
}
