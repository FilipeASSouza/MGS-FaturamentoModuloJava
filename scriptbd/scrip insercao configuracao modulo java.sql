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


        for dados in  (
            SELECT 'MGSCT_Previsoes_Contrato' AS NOMEINSTANCIA,
                   'MGSCT - Evento Previsoes Contrato' AS DESCRICAO,
                   'br.com.sankhya.mgs.ct.model.PrevisoesContratoEvento' AS CLASSNAME
                   FROM DUAL
                   UNION ALL
            SELECT 'MGSCT_Previsoes_Contrato_V' AS NOMEINSTANCIA,
                   'MGSCT - Evento Previsoes Contrato' AS DESCRICAO,
                   'br.com.sankhya.mgs.ct.model.PrevisoesContratoEvento' AS CLASSNAME
            FROM DUAL
            UNION ALL
            SELECT 'MGSCT_Vagas_Previsao_Contrato' AS NOMEINSTANCIA,
                   'MGSCT - Evento Vagas Previsao Contrato' AS DESCRICAO,
                   'br.com.sankhya.mgs.ct.evento.VagasPrevisaoContratoEvento' AS CLASSNAME
            FROM DUAL
            UNION ALL
            SELECT 'MGSCT_Previsoes_Unidade' AS NOMEINSTANCIA,
                   'MGSCT - Evento Previsoes Unidade' AS DESCRICAO,
                   'br.com.sankhya.mgs.ct.evento.PrevisoesUnidadeEvento' AS CLASSNAME
            FROM DUAL
            UNION ALL
            SELECT 'MGSCT_Previsoes_Unidade_Ser' AS NOMEINSTANCIA,
                   'MGSCT - Evento Previsoes Unidade' AS DESCRICAO,
                   'br.com.sankhya.mgs.ct.evento.PrevisoesUnidadeEvento' AS CLASSNAME
            FROM DUAL
            UNION ALL
            SELECT 'MGSCT_Vagas_Previsao_Unidade' AS NOMEINSTANCIA,
                   'MGSCT - Evento Vagas Previsao Unidade' AS DESCRICAO,
                   'br.com.sankhya.mgs.ct.evento.VagasPrevisaoUnidadeEvento' AS CLASSNAME
            FROM DUAL
            UNION ALL
            SELECT 'MGSCT_Local_Tipo_Fatura' AS NOMEINSTANCIA,
                   'MGSCT - Evento Local Tipo Fatura' AS DESCRICAO,
                   'br.com.sankhya.mgs.ct.evento.LocalTipoFaturaEvento' AS CLASSNAME
            FROM DUAL
            UNION ALL
            SELECT 'MGSCT_Taxa_Contrato' AS NOMEINSTANCIA,
                   'MGSCT - Evento Taxa Contrato' AS DESCRICAO,
                   'br.com.sankhya.mgs.ct.evento.TaxaContratoEvento' AS CLASSNAME
            FROM DUAL
            UNION ALL
            SELECT 'MGSCT_Tributos_Contrato' AS NOMEINSTANCIA,
                   'MGSCT - Evento Tributos Contrato' AS DESCRICAO,
                   'br.com.sankhya.mgs.ct.evento.TributosContratoEvento' AS CLASSNAME
            FROM DUAL
            UNION ALL
            SELECT 'MGSCT_Alocacoes_PS' AS NOMEINSTANCIA,
                   'MGSCT - Evento Alocacoes Posto' AS DESCRICAO,
                   'br.com.sankhya.mgs.ct.evento.AlocacoesPostoEvento' AS CLASSNAME
            FROM DUAL
            UNION ALL
            SELECT 'MGSCT_Alocacoes_Servicos' AS NOMEINSTANCIA,
                   'MGSCT - Evento Alocacoes Servicos' AS DESCRICAO,
                   'br.com.sankhya.mgs.ct.evento.AlocacoesServicosEvento' AS CLASSNAME
            FROM DUAL
        ) LOOP

            SELECT   COUNT ( * )
            INTO   V_CONTADOR
            FROM   TSIEVP
            WHERE   NOMEINSTANCIA = dados.NOMEINSTANCIA;

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
                          dados.NOMEINSTANCIA,
                          dados.DESCRICAO,
                          'S',
                          'RJ',
                          '<eventConfig>
      <javaCall codModulo="'|| V_CODMODULO||'" className="'||dados.CLASSNAME||'"/>
</eventConfig>');
            END IF;

        end loop;


        END IF;

        /*atualizar tgfnum com o ultimo numero*/
        UPDATE   TGFNUM
        SET   ULTCOD = V_ULTCOD
        WHERE   ARQUIVO = 'TSIEVP';
    END IF;
    COMMIT;
END;