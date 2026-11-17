// Pacote dos controllers (endpoints REST)
package br.com.fiap3espa.api.controller;

// Enum da decisão sobre um conteúdo
import br.com.fiap3espa.api.diagnostico.Situacao;
// DTOs de entrada e saída
import br.com.fiap3espa.api.diagnostico.ConfirmacaoRequest;
import br.com.fiap3espa.api.diagnostico.DiagnosticoRequest;
import br.com.fiap3espa.api.diagnostico.DiagnosticoResponse;
import br.com.fiap3espa.api.diagnostico.DiagnosticoResumoResponse;
// Serviço com o fluxo do diagnóstico
import br.com.fiap3espa.api.diagnostico.DiagnosticoService;
// Ativa a validação das anotações do DTO
import jakarta.validation.Valid;
// Injeção de dependências automática pelo Spring (@Autowired)
import org.springframework.beans.factory.annotation.Autowired;
// Resposta HTTP com status, cabeçalhos e corpo
import org.springframework.http.ResponseEntity;
// Anotações do Spring MVC:
import org.springframework.web.bind.annotation.GetMapping;      // requisições GET
import org.springframework.web.bind.annotation.PatchMapping;    // requisições PATCH (alteração parcial)
import org.springframework.web.bind.annotation.PathVariable;    // valor vindo da URL
import org.springframework.web.bind.annotation.PostMapping;     // requisições POST
import org.springframework.web.bind.annotation.RequestBody;     // corpo JSON da requisição
import org.springframework.web.bind.annotation.RequestMapping;  // prefixo de URL
import org.springframework.web.bind.annotation.RestController;  // controller REST
// Monta URLs a partir da requisição atual
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// Classes do Java:
import java.net.URI;    // endereço do recurso criado
import java.util.List;  // lista

/** Endpoints do diagnóstico: criar, consultar, histórico e confirmação do professor. */
// Controller REST
@RestController
// Os diagnósticos ficam "dentro" do aluno: /alunos/{alunoId}/diagnosticos
@RequestMapping("/alunos/{alunoId}/diagnosticos")
public class DiagnosticoController {

    // Serviço com o fluxo do diagnóstico
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private DiagnosticoService diagnosticoService;

    /**
     * Corrige as respostas, executa o modelo de IA e grava o diagnóstico.
     * 201 Created com Location; 400 se o questionário for inválido; 404 se o aluno não existir.
     */
    // POST /alunos/{alunoId}/diagnosticos
    @PostMapping
    public ResponseEntity<DiagnosticoResponse> diagnosticar(@PathVariable Long alunoId,
                                                            @RequestBody @Valid DiagnosticoRequest request) {
        // Executa o fluxo completo com as respostas recebidas
        DiagnosticoResponse criado = diagnosticoService.diagnosticar(alunoId, request.respostas());
        // Monta a URL do novo diagnóstico (ex.: /alunos/1/diagnosticos/10)
        URI local = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        // Responde 201 com o cabeçalho Location e o diagnóstico no corpo
        return ResponseEntity.created(local).body(criado);
    }

    /** Histórico resumido do aluno, do mais recente para o mais antigo. */
    // GET /alunos/{alunoId}/diagnosticos (200 ou 404)
    @GetMapping
    public List<DiagnosticoResumoResponse> historico(@PathVariable Long alunoId) {
        return diagnosticoService.historico(alunoId);
    }

    /**
     * Professor confirma um conteúdo que o modelo deixou "a confirmar". Corpo: {"situacao": "lacuna" | "sem_lacuna"}.
     * 200 com o diagnóstico atualizado; 400 situação inválida; 404 diagnóstico ou conteúdo; 409 conteúdo já decidido.
     */
    // PATCH /alunos/{alunoId}/diagnosticos/{diagnosticoId}/conteudos/{conteudo}
    @PatchMapping("/{diagnosticoId}/conteudos/{conteudo}")
    public DiagnosticoResponse confirmar(@PathVariable Long alunoId, @PathVariable Long diagnosticoId,
                                         @PathVariable String conteudo,
                                         @RequestBody @Valid ConfirmacaoRequest request) {
        // Converte o texto ("lacuna"/"sem_lacuna") no enum e confirma
        return diagnosticoService.confirmar(alunoId, diagnosticoId, conteudo, Situacao.deCodigo(request.situacao()));
    }

    /** Diagnóstico completo, no mesmo formato do POST. */
    // GET /alunos/{alunoId}/diagnosticos/{diagnosticoId} (200 ou 404)
    @GetMapping("/{diagnosticoId}")
    public DiagnosticoResponse buscar(@PathVariable Long alunoId, @PathVariable Long diagnosticoId) {
        return diagnosticoService.buscar(alunoId, diagnosticoId);
    }
}
