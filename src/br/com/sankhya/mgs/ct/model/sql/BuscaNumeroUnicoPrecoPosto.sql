SELECT NUCONTREVENTO FROM (
                              SELECT NUCONTREVENTO, NROOCORRENCIA, MAX(NROOCORRENCIA) OVER() AS MAXNROOCORRENCIA
                              FROM MGSTCTCONTRVLRPS
                              WHERE NUMCONTRATO = :NUMCONTRATO
                                AND CODTPN = :CODTPN
                                AND CODTIPOPOSTO = :CODTIPOPOSTO
                                AND MGSTCTCONTRVLRPS.CODEVENTO = DECODE( :CODEVENTO, 142, 142, 191, 191, 242, 242, (select mgstctevtapt.codevtvlr from mgstctevtapt where codevento = :CODEVENTO and rownum <= 1) )
                                AND TRUNC(SYSDATE) BETWEEN TRUNC(DTINICIO) AND TRUNC(DTFIM)
                                AND ALIQISS = ( NVL(SUBSTR(busca_tributos(:v_codunidadefatura, NVL( :v_codtipofatura, ( select codtipofatura from mgstctevtcus where codevento = :codevento and ROWNUM < 2 ) ) ,'F'),11,5)+0,0)*1 )
                          ) WHERE NROOCORRENCIA = MAXNROOCORRENCIA