-- V2: disciplinas e conteúdos do questionário, com a ordem usada pelo modelo e a estratégia de estudo
-- Fonte: python-referencia/models/modelo_info.json (conteudos_por_disciplina, ordem_saidas, nome_exibicao, estrategias)

INSERT INTO GS2627_1_disciplina (codigo, nome) VALUES ('portugues', 'Língua Portuguesa');
INSERT INTO GS2627_1_disciplina (codigo, nome) VALUES ('matematica', 'Matemática');

INSERT INTO GS2627_1_conteudo (codigo, nome, ordem_modelo, estrategia, disciplina_id)
VALUES ('decodificacao', 'Decodificação', 0,
        'Ler em voz alta todos os dias palavras e frases curtas, com jogos de sílabas e fichas de palavras; aumentar o tamanho dos textos aos poucos.',
        (SELECT id FROM GS2627_1_disciplina WHERE codigo = 'portugues'));

INSERT INTO GS2627_1_conteudo (codigo, nome, ordem_modelo, estrategia, disciplina_id)
VALUES ('localizar_informacao', 'Localizar informação', 1,
        'Ler textos curtos com perguntas cuja resposta está escrita no texto e sublinhar o trecho que responde a cada uma.',
        (SELECT id FROM GS2627_1_disciplina WHERE codigo = 'portugues'));

INSERT INTO GS2627_1_conteudo (codigo, nome, ordem_modelo, estrategia, disciplina_id)
VALUES ('inferencia', 'Inferência', 2,
        'Depois de cada leitura, conversar sobre o que não está escrito (por que o personagem agiu assim? o que vai acontecer?) e pedir que o aluno aponte as pistas no texto.',
        (SELECT id FROM GS2627_1_disciplina WHERE codigo = 'portugues'));

INSERT INTO GS2627_1_conteudo (codigo, nome, ordem_modelo, estrategia, disciplina_id)
VALUES ('ortografia', 'Ortografia', 3,
        'Fazer ditados curtos e reescrever palavras com a mesma dificuldade (s/ss, r/rr, m antes de p e b), com autocorreção ao final.',
        (SELECT id FROM GS2627_1_disciplina WHERE codigo = 'portugues'));

INSERT INTO GS2627_1_conteudo (codigo, nome, ordem_modelo, estrategia, disciplina_id)
VALUES ('numeros', 'Números', 4,
        'Usar material concreto (material dourado, ábaco, reta numérica) para contar, comparar, ordenar e entender o valor de cada algarismo.',
        (SELECT id FROM GS2627_1_disciplina WHERE codigo = 'matematica'));

INSERT INTO GS2627_1_conteudo (codigo, nome, ordem_modelo, estrategia, disciplina_id)
VALUES ('operacoes', 'Operações', 5,
        'Resolver poucos problemas por dia, primeiro com desenho ou material concreto e depois com a conta; explicar em voz alta a estratégia usada.',
        (SELECT id FROM GS2627_1_disciplina WHERE codigo = 'matematica'));

INSERT INTO GS2627_1_conteudo (codigo, nome, ordem_modelo, estrategia, disciplina_id)
VALUES ('geometria', 'Geometria', 6,
        'Manipular, desenhar e classificar formas; montar e desmontar embalagens; descrever caminhos e posições em plantas e mapas simples.',
        (SELECT id FROM GS2627_1_disciplina WHERE codigo = 'matematica'));

INSERT INTO GS2627_1_conteudo (codigo, nome, ordem_modelo, estrategia, disciplina_id)
VALUES ('grandezas_medidas', 'Grandezas e medidas', 7,
        'Medir objetos reais com régua, balança e relógio e resolver situações do dia a dia com dinheiro, tempo e comprimento.',
        (SELECT id FROM GS2627_1_disciplina WHERE codigo = 'matematica'));
