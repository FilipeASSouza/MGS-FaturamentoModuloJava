SELECT NUCONTREVENTO FROM (
                              SELECT NUCONTREVENTO, NROOCORRENCIA, MAX(NROOCORRENCIA) OVER() AS MAXNROOCORRENCIA
                              FROM MGSTCTCONTRVLRPS
                              WHERE NUMCONTRATO = :NUMCONTRATO
                                AND CODTPN = :CODTPN
                                AND CODTIPOPOSTO = :CODTIPOPOSTO
                                AND MGSTCTCONTRVLRPS.CODEVENTO = DECODE( :CODEVENTO, 142, 142, 191, 191, 242, 242, (select mgstctevtapt.codevtvlr from mgstctevtapt where codevento = :CODEVENTO and rownum <= 1) )
                                AND TRUNC(SYSDATE) BETWEEN TRUNC(DTINICIO) AND TRUNC(DTFIM)
                                AND ALIQISS = NVL( ( select mgstctcontratotrib.percinss
                                               FROM MGSTCTCONTRATOTRIB,
                                                    MGSTCTLOCALCONT,
                                                    MGSTCTLOCALTIPOFAT,
                                                    MGSTCTCONTRCENT,
                                                    mgstctcontrato,
                                                    tgfpro
                                               WHERE MGSTCTCONTRCENT.NULOCALCONT     = MGSTCTLOCALCONT.NULOCALCONT
                                                 AND MGSTCTCONTRCENT.NUMCONTRATO       = MGSTCTLOCALCONT.NUMCONTRATO
                                                 and mgstctcontrato.numcontrato = MGSTCTLOCALCONT.NUMCONTRATO
                                                 and mgstctcontrato.codtipsituacao = 1
                                                 AND MGSTCTLOCALTIPOFAT.NULOCALCONT    = MGSTCTLOCALCONT.NULOCALCONT
                                                 AND MGSTCTLOCALTIPOFAT.NUMCONTRATO    = MGSTCTLOCALCONT.NUMCONTRATO
                                                 AND MGSTCTLOCALTIPOFAT.NULOCALTIPOFAT = MGSTCTCONTRATOTRIB.NULOCALTIPOFAT
                                                 AND MGSTCTCONTRATOTRIB.CODPROD        = tgfpro.CODPROD
                                                 AND MGSTCTCONTRATOTRIB.ATIVO          = 'S'
                                                 AND nvl(MGSTCTCONTRATOTRIB.DTFIM,sysdate)         >= sysdate
                                                 AND nvl(MGSTCTCONTRCENT.DTfim,sysdate)            >= SYSDATE
                                                 AND MGSTCTCONTRCENT.CODSITE = :v_codunidadefatura
                                                 and mgstctlocaltipofat.codtipofatura  = :v_codtipofatura) , 0 )

                          ) WHERE NROOCORRENCIA = MAXNROOCORRENCIA