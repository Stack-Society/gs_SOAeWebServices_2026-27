// Pacote dos controllers (endpoints REST)
package br.com.fiap3espa.api.controller;

// DTOs de entrada e saída
import br.com.fiap3espa.api.escola.EscolaAtualizacaoRequest;
import br.com.fiap3espa.api.escola.EscolaRequest;
import br.com.fiap3espa.api.escola.EscolaResponse;
// Serviço com as regras de escolas
import br.com.fiap3espa.api.escola.EscolaService;
// Ativa a validação das anotações do DTO
import jakarta.validation.Valid;
// Injeção de dependências automática pelo Spring (@Autowired)
import org.springframework.beans.factory.annotation.Autowired;
// Resposta HTTP com status, cabeçalhos e corpo
import org.springframework.http.ResponseEntity;
// Paginação do Spring Data:
import org.springframework.data.domain.Page;      // uma página de resultados (conteúdo + totais)
import org.springframework.data.domain.Pageable;  // pedido de página: número, tamanho e ordenação
// Valores padrão da paginação quando a URL não informa page/size/sort
import org.springframework.data.web.PageableDefault;
// Anotações do Spring MVC:
import org.springframework.web.bind.annotation.DeleteMapping;   // requisições DELETE
import org.springframework.web.bind.annotation.GetMapping;      // requisições GET
import org.springframework.web.bind.annotation.PathVariable;    // valor vindo da URL
import org.springframework.web.bind.annotation.PostMapping;     // requisições POST
import org.springframework.web.bind.annotation.PutMapping;      // requisições PUT
import org.springframework.web.bind.annotation.RequestBody;     // corpo JSON da requisição
import org.springframework.web.bind.annotation.RequestMapping;  // prefixo de URL do controller
import org.springframework.web.bind.annotation.RestController;  // controller REST
// Monta URLs a partir da requisição atual
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// Classes do Java:
import java.net.URI;    // endereço do recurso criado

/** Endpoints do cadastro de escolas (CRUD). */
// Controller REST: o retorno de cada método é convertido em JSON
@RestController
// Todas as rotas começam com /escolas
@RequestMapping("/escolas")
public class EscolaController {

    // Serviço com as regras de negócio
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private EscolaService escolaService;

    // GET /escolas → lista paginada (200); aceita ?page=0&size=10&sort=codigo na URL
    @GetMapping
    public Page<EscolaResponse> listar(
            // Sem parâmetros na URL: página 0, 10 registros, ordenados por código
            @PageableDefault(size = 10, sort = "codigo") Pageable paginacao) {
        return escolaService.listar(paginacao);
    }

    // GET /escolas/{id} → busca uma escola (200 ou 404)
    @GetMapping("/{id}")
    public EscolaResponse buscar(@PathVariable Long id) {
        return escolaService.buscar(id);
    }

    /** 201 Created, com o endereço da nova escola no cabeçalho Location. */
    // POST /escolas → cadastra uma escola (201, 400 ou 409)
    @PostMapping
    public ResponseEntity<EscolaResponse> criar(@RequestBody @Valid EscolaRequest request) {
        // Cria a escola
        EscolaResponse criada = escolaService.criar(request);
        // Monta a URL da nova escola (ex.: http://localhost:8080/escolas/3)
        URI local = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.id()).toUri();
        // Responde 201 com o cabeçalho Location e a escola no corpo
        return ResponseEntity.created(local).body(criada);
    }

    // PUT /escolas/{id} → atualiza o nome (200, 400 ou 404)
    @PutMapping("/{id}")
    public EscolaResponse atualizar(@PathVariable Long id, @RequestBody @Valid EscolaAtualizacaoRequest request) {
        return escolaService.atualizar(id, request);
    }

    /** 204 No Content; 409 se a escola ainda tiver alunos. */
    // DELETE /escolas/{id} → exclui a escola
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        // Exclui (ou lança 404/409)
        escolaService.excluir(id);
        // Responde 204 sem corpo
        return ResponseEntity.noContent().build();
    }
}
