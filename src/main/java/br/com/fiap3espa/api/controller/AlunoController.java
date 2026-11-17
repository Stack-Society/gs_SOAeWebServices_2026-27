// Pacote dos controllers (endpoints REST)
package br.com.fiap3espa.api.controller;

// DTOs de entrada e saída
import br.com.fiap3espa.api.aluno.AlunoAtualizacaoRequest;
import br.com.fiap3espa.api.aluno.AlunoRequest;
import br.com.fiap3espa.api.aluno.AlunoResponse;
// Serviço com as regras de alunos
import br.com.fiap3espa.api.aluno.AlunoService;
// Ativa a validação das anotações do DTO (@NotBlank, @Min...)
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
import org.springframework.web.bind.annotation.PathVariable;    // valor vindo da URL (ex.: /alunos/5)
import org.springframework.web.bind.annotation.PostMapping;     // requisições POST
import org.springframework.web.bind.annotation.PutMapping;      // requisições PUT
import org.springframework.web.bind.annotation.RequestBody;     // corpo JSON da requisição
import org.springframework.web.bind.annotation.RequestMapping;  // prefixo de URL do controller
import org.springframework.web.bind.annotation.RestController;  // controller REST (retorno vira JSON)
// Monta URLs a partir da requisição atual
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// Classes do Java:
import java.net.URI;    // endereço do recurso criado

/** Endpoints do cadastro de alunos (CRUD). */
// Controller REST: o retorno de cada método é convertido em JSON
@RestController
// Todas as rotas começam com /alunos
@RequestMapping("/alunos")
public class AlunoController {

    // Serviço com as regras de negócio
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private AlunoService alunoService;

    // GET /alunos → lista paginada (200); aceita ?page=0&size=10&sort=codigo na URL
    @GetMapping
    public Page<AlunoResponse> listar(
            // Sem parâmetros na URL: página 0, 10 registros, ordenados por código
            @PageableDefault(size = 10, sort = "codigo") Pageable paginacao) {
        return alunoService.listar(paginacao);
    }

    // GET /alunos/{id} → busca um aluno (200 ou 404)
    @GetMapping("/{id}")
    public AlunoResponse buscar(@PathVariable Long id) {
        return alunoService.buscar(id);
    }

    /** 201 Created, com o endereço do novo aluno no cabeçalho Location. */
    // POST /alunos → cadastra um aluno (201, 400, 404 ou 409)
    @PostMapping
    public ResponseEntity<AlunoResponse> criar(@RequestBody @Valid AlunoRequest request) {
        // Cria o aluno
        AlunoResponse criado = alunoService.criar(request);
        // Monta a URL do novo aluno (ex.: http://localhost:8080/alunos/7)
        URI local = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        // Responde 201 com o cabeçalho Location e o aluno no corpo
        return ResponseEntity.created(local).body(criado);
    }

    // PUT /alunos/{id} → atualiza série e escola (200, 400 ou 404)
    @PutMapping("/{id}")
    public AlunoResponse atualizar(@PathVariable Long id, @RequestBody @Valid AlunoAtualizacaoRequest request) {
        return alunoService.atualizar(id, request);
    }

    /** 204 No Content; 409 se o aluno já tiver diagnósticos. */
    // DELETE /alunos/{id} → exclui o aluno
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        // Exclui (ou lança 404/409)
        alunoService.excluir(id);
        // Responde 204 sem corpo
        return ResponseEntity.noContent().build();
    }
}
