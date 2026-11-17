// Pacote da funcionalidade de escolas: entidade, DTOs, repositório e service
package br.com.fiap3espa.api.escola;

// Exceções que viram 409 e 404
import br.com.fiap3espa.api.exception.ConflitoException;
import br.com.fiap3espa.api.exception.RecursoNaoEncontradoException;
// Repositório de alunos (verifica se a escola tem alunos antes de excluir)
import br.com.fiap3espa.api.aluno.AlunoRepository;
// Injeção de dependências automática pelo Spring (@Autowired)
import org.springframework.beans.factory.annotation.Autowired;
// Paginação do Spring Data:
import org.springframework.data.domain.Page;      // uma página de resultados (conteúdo + totais)
import org.springframework.data.domain.Pageable;  // pedido de página: número, tamanho e ordenação
// Serviço gerenciado pelo Spring
import org.springframework.stereotype.Service;
// Controle de transação
import org.springframework.transaction.annotation.Transactional;

/** Regras do cadastro de escolas (CRUD). */
@Service
public class EscolaService {

    // Acesso à tabela de escolas
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private EscolaRepository escolaRepository;

    // Usado para verificar se a escola tem alunos antes de excluir
    // @Autowired: o Spring injeta a instância automaticamente no campo
    @Autowired
    private AlunoRepository alunoRepository;

    // Lista os escolas uma página por vez; readOnly: transação só de leitura (mais leve)
    @Transactional(readOnly = true)
    public Page<EscolaResponse> listar(Pageable paginacao) {
        // Busca a página pedida e converte cada escola em EscolaResponse (Page.map mantém os totais)
        return escolaRepository.findAll(paginacao).map(EscolaResponse::new);
    }

    // Busca uma escola pelo id (404 se não existir)
    @Transactional(readOnly = true)
    public EscolaResponse buscar(Long id) {
        return new EscolaResponse(buscarEntidade(id));
    }

    // Cadastra uma escola
    @Transactional
    public EscolaResponse criar(EscolaRequest request) {
        // Recusa código repetido com 409
        if (escolaRepository.existsByCodigo(request.codigo())) {
            throw new ConflitoException("Já existe escola com o código " + request.codigo());
        }
        // Cria, grava e devolve a escola com o id gerado
        return new EscolaResponse(escolaRepository.save(new Escola(request.codigo(), request.nome())));
    }

    // Atualiza o nome de uma escola
    @Transactional
    public EscolaResponse atualizar(Long id, EscolaAtualizacaoRequest request) {
        // Busca a escola (404 se não existir)
        Escola escola = buscarEntidade(id);
        // Altera o nome; o Hibernate grava no commit
        escola.atualizar(request.nome());
        // Devolve a escola atualizada
        return new EscolaResponse(escola);
    }

    // Exclui uma escola
    @Transactional
    public void excluir(Long id) {
        // Busca a escola (404 se não existir)
        Escola escola = buscarEntidade(id);
        // Escola com alunos não pode ser excluída (409)
        if (alunoRepository.existsByEscolaId(id)) {
            throw new ConflitoException("A escola " + escola.getCodigo() + " tem alunos e não pode ser excluída");
        }
        // Remove do banco
        escolaRepository.delete(escola);
    }

    /** Usado também pelo cadastro de alunos. */
    // Público porque o AlunoService fica em outro pacote; devolve a entidade
    public Escola buscarEntidade(Long id) {
        // Busca pelo id; se não existir, lança 404
        return escolaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Escola " + id + " não encontrada"));
    }
}
