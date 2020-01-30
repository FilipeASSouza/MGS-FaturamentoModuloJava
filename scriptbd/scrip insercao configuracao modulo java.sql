DECLARE
    V_ULTCOD      NUMBER;
    V_CONTADOR    NUMBER;
    V_CODMODULO   NUMBER;
    V_ERRMSG      VARCHAR2 (4000);
BEGIN
    /*Eventos*/
    SELECT   ULTCOD
    INTO   V_ULTCOD
    FROM   TGFNUM
    WHERE   ARQUIVO = 'TSIEVP';

    SELECT   COUNT ( * )
    INTO   V_CONTADOR
    FROM   TSIMOD
    WHERE   LOWER (RESOURCEID) = 'br.com.sankhya.mgs.ct';

    IF (V_CONTADOR = 0)
    THEN
        RAISE_APPLICATION_ERROR (-20101, 'Modulo java n�o esta cadastrado!!!');
    ELSE
        SELECT   CODMODULO
        INTO   V_CODMODULO
        FROM   TSIMOD
        WHERE   LOWER (RESOURCEID) = 'br.com.sankhya.mgs.ct';


        /*Evento MGSCT_Previsoes_Contrato*/
        SELECT   COUNT ( * )
        INTO   V_CONTADOR
        FROM   TSIEVP
        WHERE   NOMEINSTANCIA = 'MGSCT_Previsoes_Contrato';

        IF (V_CONTADOR = 0)
        THEN
            V_ULTCOD := V_ULTCOD + 1;

            INSERT INTO TSIEVP (NUEVENTO,
                                NOMEINSTANCIA,
                                DESCRICAO,
                                ATIVO,
                                TIPO,
                                CONFIG)
            VALUES   (V_ULTCOD,
                      'MGSCT_Previsoes_Contrato',
                      'MGSCT - Evento Previsoes Contrato',
                      'S',
                      'RJ',
                      '<eventConfig><javaCall codModulo="'
                          || V_CODMODULO
                          || '" className="br.com.sankhya.mgs.ct.model.PrevisoesContratoEvento"/></eventConfig>');
        END IF;

/*MGSCT_Previsoes_Contrato_V*/
        SELECT   COUNT ( * )
        INTO   V_CONTADOR
        FROM   TSIEVP
        WHERE   NOMEINSTANCIA = 'MGSCT_Previsoes_Contrato_V';

        IF (V_CONTADOR = 0)
        THEN
            V_ULTCOD := V_ULTCOD + 1;

            INSERT INTO TSIEVP (NUEVENTO,
                                NOMEINSTANCIA,
                                DESCRICAO,
                                ATIVO,
                                TIPO,
                                CONFIG)
            VALUES   (V_ULTCOD,
                      'MGSCT_Previsoes_Contrato_V',
                      'MGSCT - Evento Previsoes Contrato',
                      'S',
                      'RJ',
                      '<eventConfig><javaCall codModulo="'
                          || V_CODMODULO
                          || '" className="br.com.sankhya.mgs.ct.model.PrevisoesContratoEvento"/></eventConfig>');
        END IF;

/*MGSCT_Vagas_Previsao_Contrato*/
        SELECT   COUNT ( * )
        INTO   V_CONTADOR
        FROM   TSIEVP
        WHERE   NOMEINSTANCIA = 'MGSCT_Vagas_Previsao_Contrato';

        IF (V_CONTADOR = 0)
        THEN
            V_ULTCOD := V_ULTCOD + 1;

            INSERT INTO TSIEVP (NUEVENTO,
                                NOMEINSTANCIA,
                                DESCRICAO,
                                ATIVO,
                                TIPO,
                                CONFIG)
            VALUES   (V_ULTCOD,
                      'MGSCT_Vagas_Previsao_Contrato',
                      'MGSCT - Evento Vagas Previsao Contrato',
                      'S',
                      'RJ',
                      '<eventConfig><javaCall codModulo="'
                          || V_CODMODULO
                          || '" className="br.com.sankhya.mgs.ct.evento.VagasPrevisaoContratoEvento"/></eventConfig>');
        END IF;

/*MGSCT_Previsoes_Unidade*/
        SELECT   COUNT ( * )
        INTO   V_CONTADOR
        FROM   TSIEVP
        WHERE   NOMEINSTANCIA = 'MGSCT_Previsoes_Unidade';

        IF (V_CONTADOR = 0)
        THEN
            V_ULTCOD := V_ULTCOD + 1;

            INSERT INTO TSIEVP (NUEVENTO,
                                NOMEINSTANCIA,
                                DESCRICAO,
                                ATIVO,
                                TIPO,
                                CONFIG)
            VALUES   (V_ULTCOD,
                      'MGSCT_Previsoes_Unidade',
                      'MGSCT - Evento Previsoes Unidade',
                      'S',
                      'RJ',
                      '<eventConfig><javaCall codModulo="'
                          || V_CODMODULO
                          || '" className="br.com.sankhya.mgs.ct.evento.PrevisoesUnidadeEvento"/></eventConfig>');
        END IF;

        /*MGSCT_Previsoes_Unidade_Ser*/
        SELECT   COUNT ( * )
        INTO   V_CONTADOR
        FROM   TSIEVP
        WHERE   NOMEINSTANCIA = 'MGSCT_Previsoes_Unidade_Ser';

        IF (V_CONTADOR = 0)
        THEN
            V_ULTCOD := V_ULTCOD + 1;

            INSERT INTO TSIEVP (NUEVENTO,
                                NOMEINSTANCIA,
                                DESCRICAO,
                                ATIVO,
                                TIPO,
                                CONFIG)
            VALUES   (V_ULTCOD,
                      'MGSCT_Previsoes_Unidade_Ser',
                      'MGSCT - Evento Previsoes Unidade',
                      'S',
                      'RJ',
                      '<eventConfig><javaCall codModulo="'
                          || V_CODMODULO
                          || '" className="br.com.sankhya.mgs.ct.evento.PrevisoesUnidadeEvento"/></eventConfig>');
        END IF;


        /*MGSCT_Vagas_Previsao_Unidade*/

        SELECT   COUNT ( * )
        INTO   V_CONTADOR
        FROM   TSIEVP
        WHERE   NOMEINSTANCIA = 'MGSCT_Vagas_Previsao_Unidade';

        IF (V_CONTADOR = 0)
        THEN
            V_ULTCOD := V_ULTCOD + 1;

            INSERT INTO TSIEVP (NUEVENTO,
                                NOMEINSTANCIA,
                                DESCRICAO,
                                ATIVO,
                                TIPO,
                                CONFIG)
            VALUES   (V_ULTCOD,
                      'MGSCT_Vagas_Previsao_Unidade',
                      'MGSCT - Evento Vagas Previsao Unidade',
                      'S',
                      'RJ',
                      '<eventConfig><javaCall codModulo="'
                          || V_CODMODULO
                          || '" className="br.com.sankhya.mgs.ct.evento.VagasPrevisaoUnidadeEvento"/></eventConfig>');
        END IF;

        /*atualizar tgfnum com o ultimo numero*/
        UPDATE   TGFNUM
        SET   ULTCOD = V_ULTCOD
        WHERE   ARQUIVO = 'TSIEVP';
    END IF;
    COMMIT;
END;