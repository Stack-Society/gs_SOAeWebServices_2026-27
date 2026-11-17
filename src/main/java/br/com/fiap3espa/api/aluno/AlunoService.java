// Pacote da funcionalidade de alunos: entidade, DTOs, repositório e service
package br.com.fiap3espa.api.aluno;

// Service de escolas, usado para buscar a escola informada no cadastro
import br.com.fiap3espa.api.escola.EscolaService;

// Exceções que viram 409 e 404
import br.com.fiap3espa.api.exception.ConflitoException;
import br.com.fiap3espa.api.exception.RecursoNaoEncontradoException;
// Injeção de dependências automática pelo Spring (@Autowired)
import org.springframework.beans.factory.annotation.Autowired;
// Paginação do Spring Data:
import org.springframework.data.domain.Page;      // uma página de resultados (conteúdo + totais)
import org.springframework.data.domain.Pageable;  // pedido de página: número, tamanho e ordenação
// Marca a classe como serviço gerenciado pelo Spring
import org.springframework.stereotype.Service;
// Controle de transação com o banco
import org.springframework.transaction.annotation.Transactional;

/** Regras do cadastro de alunos (CRUD). */
// O Spring cria uma única instância e a injeta onde for pedida
@Service
public class AlunoService {

    // Acesso à tabela de alunos
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private AlunoRepository alunoRepository;

    // Usado para buscar a escola informada no cadastro
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private EscolaService escolaService;

    // Lista os alunos uma página por vez; readOnly: transação só de leitura (mais leve)
    @Transactional(readOnly = true)
    public Page<AlunoResponse> listar(Pageable paginacao) {
        // Busca a página pedida e converte cada aluno em AlunoResponse (Page.map mantém os totais)
        return alunoRepository.findAll(paginacao).map(AlunoResponse::new);
    }

    // Busca um aluno pelo id (404 se não existir)
    @Transactional(readOnly = true)
    public AlunoResponse buscar(Long id) {
        return new AlunoResponse(buscarEntidade(id));
    }

    // Cadastra um aluno; a transação faz commit no fim do método ou rollback se houver exceção
    @Transactional
    public AlunoResponse criar(AlunoRequest request) {
        // Recusa código repetido com 409 (antes de o banco recusar pela restrição UNIQUE)
        if (alunoRepository.existsByCodigo(request.codigo())) {
            throw new ConflitoException("Já existe aluno com o código " + request.codigo());
        }
        // Cria a entidade; buscarEntidade da escola responde 404 se a escola não existir
        Aluno aluno = new Aluno(request.codigo(), request.serie(), escolaService.buscarEntidade(request.escolaId()));
        // Grava no banco e devolve o aluno com o id gerado
        return new AlunoResponse(alunoRepository.save(aluno));
    }

    // Atualiza série e escola de um aluno
    @Transactional
    public AlunoResponse atualizar(Long id, AlunoAtualizacaoRequest request) {
        // Busca o aluno (404 se não existir)
        Aluno aluno = buscarEntidade(id);
        // Altera a entidade; o Hibernate grava a mudança sozinho no commit (dirty checking)
        aluno.atualizar(request.serie(), escolaService.buscarEntidade(request.escolaId()));
        // Devolve o aluno atualizado
        return new AlunoResponse(aluno);
    }

    // Exclui um aluno
    @Transactional
    public void excluir(Long id) {
        // Busca o aluno (404 se não existir)
        Aluno aluno = buscarEntidade(id);
        // Remove do banco
        alunoRepository.delete(aluno);
    }

    /** Usado também pelo diagnóstico. */
    // Público porque o DiagnosticoService fica em outro pacote; devolve a entidade (não o DTO)
    public Aluno buscarEntidade(Long id) {
        // Busca já com a escola carregada; se não existir, lança 404
        return alunoRepository.findComEscolaById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno " + id + " não encontrado"));
    }
}
