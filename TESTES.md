# Roteiro de testes manuais — AprendIA API

Testes manuais da API com o Insomnia, usando a coleção `insomnia/aprendia-insomnia.json`.
Cada caso (CT) indica a requisição da coleção, o que é enviado e o resultado esperado. Marque o resultado e anexe o print como evidência.

## Preparação

1. Subir a API com o banco (ver "Como executar" no README). Ela deve responder em `http://localhost:8080`.
2. Importar `insomnia/aprendia-insomnia.json` no Insomnia.
3. Executar os casos **na ordem** abaixo: os primeiros criam a escola, o aluno e o diagnóstico usados pelos seguintes.
4. Depois do CT03, CT10 e CT18, conferir o `id` devolvido e, se não for 1, ajustar no *Base Environment* as variáveis `escola_id`, `aluno_id` e `diagnostico_id`.
5. Se o banco já tiver dados de uma execução anterior, os casos de criação (CT03, CT10) vão responder 409. Nesse caso, use códigos novos (ex.: `E02`, `A03504`) ou aponte as variáveis para os ids existentes.

Legenda do resultado: ☐ não executado · ✅ aprovado · ❌ reprovado.

Todos os erros da API (exceto CT02) seguem o formato:
`{ "status": ..., "mensagem": "...", "detalhes": [...], "dataHora": "..." }`

As mensagens de validação (`@NotBlank`, `@Min`, `@Max`) vêm do Bean Validation e podem variar com o idioma do sistema; nesses casos, o roteiro confere o campo citado, não o texto exato.

As listagens `GET /escolas` e `GET /alunos` são paginadas: a resposta é uma página do Spring Data, com os registros em `content` e os totais em `totalElements`, `totalPages`, `size` e `number`. Sem parâmetros na URL, vêm a página 0, com 10 registros, ordenados por `codigo`.

---

## 0. Health check

| CT | Requisição na coleção | Envio | Resultado esperado | Resultado | Evidência |
|---|---|---|---|---|---|
| CT37 | Health check (200) | `GET /healthcheck` | **200**; corpo em texto = "Teste de integridade da API AprendIA." | ☐ | |

## 1. Questionário

| CT | Requisição na coleção | Envio | Resultado esperado | Resultado | Evidência |
|---|---|---|---|---|---|
| CT01 | Questionário do 3º ano (200) | `GET /questionarios/3` | **200**; `serie` = 3; `totalQuestoes` = 24; `questoes[0]` = `{ "numero": 1, "conteudo": "decodificacao", "conteudoNome": "Decodificação", "disciplina": "Língua Portuguesa" }`; nenhum campo de gabarito | ☐ | |
| CT02 | Série inexistente (400) | `GET /questionarios/9` | **400** (validação do parâmetro da URL; corpo no formato padrão do Spring) | ☐ | |

## 2. Escolas

| CT | Requisição na coleção | Envio | Resultado esperado | Resultado | Evidência |
|---|---|---|---|---|---|
| CT03 | Criar escola (201) | `POST /escolas` `{"codigo": "E01", "nome": "EMEF Exemplo"}` | **201**; corpo com `id`, `codigo` = "E01", `nome` = "EMEF Exemplo"; cabeçalho `Location` = `.../escolas/{id}` | ☐ | |
| CT04 | Criar escola com código repetido (409) | `POST /escolas` `{"codigo": "E01", ...}` | **409**; `mensagem` = "Já existe escola com o código E01" | ☐ | |
| CT05 | Criar escola sem nome (400) | `POST /escolas` `{"codigo": "E02", "nome": ""}` | **400**; `mensagem` = "Dados inválidos"; `detalhes` com um item começando por `nome:`; a escola E02 **não** é criada | ☐ | |
| CT06 | Listar escolas (200) | `GET /escolas` | **200**; página com `content` contendo a escola E01; `size` = 10; `number` = 0 | ☐ | |
| CT38 | Listar escolas - página com 1 item (200) | `GET /escolas?page=0&size=1&sort=codigo,desc` | **200**; `content` com 1 escola; `size` = 1; `number` = 0; `totalElements` = total de escolas cadastradas | ☐ | |
| CT07 | Buscar escola (200) | `GET /escolas/{{escola_id}}` | **200**; escola E01 | ☐ | |
| CT08 | Buscar escola inexistente (404) | `GET /escolas/999999` | **404**; `mensagem` = "Escola 999999 não encontrada" | ☐ | |
| CT09 | Atualizar escola (200) | `PUT /escolas/{{escola_id}}` `{"nome": "EMEF Exemplo - Unidade Centro"}` | **200**; `nome` atualizado; `codigo` continua "E01" | ☐ | |

## 3. Alunos

| CT | Requisição na coleção | Envio | Resultado esperado | Resultado | Evidência |
|---|---|---|---|---|---|
| CT10 | Criar aluno do 4º ano (201) | `POST /alunos` `{"codigo": "A03503", "serie": 4, "escolaId": {{escola_id}}}` | **201**; `codigo` = "A03503"; `serie` = 4; `escola.codigo` = "E01"; cabeçalho `Location` = `.../alunos/{id}` | ☐ | |
| CT11 | Criar aluno com código repetido (409) | `POST /alunos` com o mesmo código | **409**; `mensagem` = "Já existe aluno com o código A03503" | ☐ | |
| CT12 | Criar aluno com série inválida (400) | `POST /alunos` `{"codigo": "A99999", "serie": 9, ...}` | **400**; `detalhes` com um item começando por `serie:`; aluno **não** criado | ☐ | |
| CT13 | Criar aluno em escola inexistente (404) | `POST /alunos` `{"codigo": "A99998", "serie": 3, "escolaId": 999999}` | **404**; `mensagem` = "Escola 999999 não encontrada"; aluno **não** criado | ☐ | |
| CT14 | Listar alunos (200) | `GET /alunos` | **200**; página com `content` contendo A03503 com a escola E01; `size` = 10; `number` = 0 | ☐ | |
| CT39 | Listar alunos - página com 1 item (200) | `GET /alunos?page=0&size=1` | **200**; `content` com 1 aluno (com a escola); `size` = 1; `totalElements` = total de alunos cadastrados | ☐ | |
| CT15 | Buscar aluno (200) | `GET /alunos/{{aluno_id}}` | **200**; aluno A03503 | ☐ | |
| CT16 | Buscar aluno inexistente (404) | `GET /alunos/999999` | **404**; `mensagem` = "Aluno 999999 não encontrado" | ☐ | |
| CT17 | Atualizar aluno (200) | `PUT /alunos/{{aluno_id}}` `{"serie": 4, "escolaId": {{escola_id}}}` | **200**; `serie` = 4; `codigo` continua "A03503" | ☐ | |

## 4. Diagnósticos (modelo de IA)

O CT18 usa as respostas do golden test **A03503** do notebook de IA. Os valores esperados abaixo são os do notebook.

| CT | Requisição na coleção | Envio | Resultado esperado | Resultado | Evidência |
|---|---|---|---|---|---|
| CT18 | Diagnosticar - golden test A03503 (201) | `POST /alunos/{{aluno_id}}/diagnosticos` com as 24 respostas | **201**; cabeçalho `Location`; `serie` = 4; `versaoModelo` = "2.0"; `mensagem` = "4 lacuna(s) encontrada(s)."; valores da tabela **A** abaixo | ☐ | |
| CT19 | Questionário com resposta inválida (400) | mesmo corpo, com `"q1": "D"` | **400**; `mensagem` = "Questionário inválido"; `detalhes` = ["q1: resposta inválida 'D'"]; nenhum diagnóstico gravado | ☐ | |
| CT20 | Questionário com questão em branco (400) | mesmo corpo, com `"q5": ""` | **400**; `detalhes` = ["q5: sem resposta"]; nenhum diagnóstico gravado | ☐ | |
| CT21 | Diagnosticar aluno inexistente (404) | `POST /alunos/999999/diagnosticos` | **404**; `mensagem` = "Aluno 999999 não encontrado" | ☐ | |
| CT22 | Histórico do aluno (200) | `GET /alunos/{{aluno_id}}/diagnosticos` | **200**; 1 item (só o do CT18: CT19 e CT20 não gravaram nada); `lacunas` = 4; `aConfirmar` = 2; `niveis` = portugues "em desenvolvimento", matematica "a confirmar" | ☐ | |
| CT23 | Buscar diagnóstico (200) | `GET /alunos/{{aluno_id}}/diagnosticos/{{diagnostico_id}}` | **200**; corpo igual ao do CT18 | ☐ | |
| CT24 | Buscar diagnóstico inexistente (404) | `GET /alunos/{{aluno_id}}/diagnosticos/999999` | **404**; `mensagem` = "Diagnóstico 999999 do aluno {id} não encontrado" | ☐ | |

### Tabela A — resultado esperado do CT18

| Conteúdo | Acertos | Probabilidade | `situacao` |
|---|---|---|---|
| decodificacao | 3 | 0.226029 | sem_lacuna |
| localizar_informacao | 1 | 0.790741 | lacuna |
| inferencia | 2 | 0.605664 | lacuna |
| ortografia | 3 | 0.263747 | sem_lacuna |
| numeros | 1 | 0.786955 | lacuna |
| operacoes | 1 | 0.827467 | lacuna |
| geometria | 3 | 0.374864 | a_confirmar |
| grandezas_medidas | 3 | 0.395532 | a_confirmar |

- `disciplinas`: portugues = **em desenvolvimento** (lacunas: localizar_informacao, inferencia); matematica = **a confirmar** (lacunas: numeros, operacoes; aConfirmar: geometria, grandezas_medidas).
- `reforcar`, nesta ordem: operacoes, localizar_informacao, numeros, inferencia, grandezas_medidas, geometria (lacunas primeiro; depois os "a confirmar"; em cada grupo, da maior para a menor probabilidade).
- Em todos os conteúdos: `situacaoProfessor` = null e `situacaoFinal` = `situacao`.

## 5. Confirmação do professor

| CT | Requisição na coleção | Envio | Resultado esperado | Resultado | Evidência |
|---|---|---|---|---|---|
| CT25 | Professor confirma geometria como lacuna (200) | `PATCH .../diagnosticos/{{diagnostico_id}}/conteudos/geometria` `{"situacao": "lacuna"}` | **200**; geometria: `situacao` = "a_confirmar", `situacaoProfessor` = "lacuna", `situacaoFinal` = "lacuna"; matematica = **abaixo do esperado** (3 lacunas; a dúvida restante não muda mais o nível); `mensagem` = "5 lacuna(s) encontrada(s)." | ☐ | |
| CT26 | Professor confirma grandezas_medidas sem lacuna (200) | `PATCH .../conteudos/grandezas_medidas` `{"situacao": "sem_lacuna"}` | **200**; grandezas_medidas: `situacaoProfessor` = "sem_lacuna", `situacaoFinal` = "sem_lacuna"; matematica continua **abaixo do esperado**, sem conteúdos em `aConfirmar`; `reforcar` = operacoes, localizar_informacao, numeros, inferencia, geometria | ☐ | |
| CT27 | Confirmar conteúdo já decidido pelo modelo (409) | `PATCH .../conteudos/numeros` `{"situacao": "sem_lacuna"}` | **409**; `mensagem` = "O conteúdo numeros já foi decidido pelo modelo (lacuna) e não precisa de confirmação" | ☐ | |
| CT28 | Confirmar com situação inválida (400) | `PATCH .../conteudos/geometria` `{"situacao": "talvez"}` | **400**; `detalhes` = ["situacao: deve ser lacuna ou sem_lacuna"] | ☐ | |
| CT29 | Buscar diagnóstico (200) — repetir depois das confirmações | `GET /alunos/{{aluno_id}}/diagnosticos/{{diagnostico_id}}` | **200**; as confirmações do CT25 e CT26 continuam gravadas; `situacao` do modelo continua "a_confirmar" em geometria e grandezas_medidas | ☐ | |
| CT30 | Histórico do aluno (200) — repetir | `GET /alunos/{{aluno_id}}/diagnosticos` | **200**; `lacunas` = 5; `aConfirmar` = 0; matematica = "abaixo do esperado" | ☐ | |

## 6. Exclusões

| CT | Requisição na coleção | Envio | Resultado esperado | Resultado | Evidência |
|---|---|---|---|---|---|
| CT31 | Excluir escola com alunos (409) | `DELETE /escolas/{{escola_id}}` | **409**; `mensagem` = "A escola E01 tem alunos e não pode ser excluída" | ☐ | |
| CT32 | Excluir aluno com diagnósticos (409) | `DELETE /alunos/{{aluno_id}}` | **409**; `mensagem` = "O aluno A03503 tem diagnósticos e não pode ser excluído" | ☐ | |
| CT33 | Excluir aluno inexistente (404) | `DELETE /alunos/999999` | **404**; `mensagem` = "Aluno 999999 não encontrado" | ☐ | |

## 7. Conferência no banco (opcional, SQL Developer)

| CT | Consulta | Resultado esperado | Resultado | Evidência |
|---|---|---|---|---|
| CT34 | `SELECT version, description, success FROM "flyway_schema_history" ORDER BY installed_rank;` | versões 0 (baseline) e 1 a 6, todas com `success` = 1 | ☐ | |
| CT35 | `SELECT COUNT(*) FROM GS2627_1_resposta r JOIN GS2627_1_diagnostico d ON d.id = r.diagnostico_id JOIN GS2627_1_aluno a ON a.id = d.aluno_id WHERE a.codigo = 'A03503';` | 24 (só o diagnóstico do CT18 foi gravado) | ☐ | |
| CT36 | `SELECT c.codigo, rc.situacao, rc.situacao_professor FROM GS2627_1_resultado_conteudo rc JOIN GS2627_1_conteudo c ON c.id = rc.conteudo_id WHERE rc.situacao_professor IS NOT NULL;` | geometria (A_CONFIRMAR, LACUNA) e grandezas_medidas (A_CONFIRMAR, SEM_LACUNA) | ☐ | |

## Resumo da execução

| Total | Aprovados | Reprovados | Não executados |
|---|---|---|---|
| 39 | | | |

Data da execução: ____ · Responsável: ____
