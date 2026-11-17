-- V6: confirmação do professor para os conteúdos que o modelo deixou "a confirmar"
-- A decisão do modelo (situacao) não muda; a do professor fica em colunas próprias, para auditoria.

ALTER TABLE GS2627_1_resultado_conteudo ADD (
    situacao_professor  VARCHAR2(20),
    data_confirmacao    TIMESTAMP,
    CONSTRAINT ck_resultado_sit_professor CHECK (situacao_professor IN ('LACUNA', 'SEM_LACUNA')),
    CONSTRAINT ck_resultado_so_a_confirmar CHECK (situacao_professor IS NULL OR situacao = 'A_CONFIRMAR'),
    CONSTRAINT ck_resultado_data_confirmacao CHECK (
        (situacao_professor IS NULL AND data_confirmacao IS NULL)
        OR (situacao_professor IS NOT NULL AND data_confirmacao IS NOT NULL))
);
