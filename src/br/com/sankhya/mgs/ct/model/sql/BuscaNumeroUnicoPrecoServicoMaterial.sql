SELECT NUCONTRMATSRV FROM (
SELECT NUCONTRMATSRV, NROOCORENCIA, MAX(NROOCORENCIA) OVER() AS MAXNROOCORENCIA
FROM MGSTCTCONTRVLRSERV
WHERE NUMCONTRATO = :NUMCONTRATO
AND NUMODALIDADE = :NUMODALIDADE
AND CODSERVMATERIAL = :CODSERVMATERIAL
AND CODEVENTO = :CODEVENTO
AND TRUNC(SYSDATE) BETWEEN DTINICIO AND DTFIM
AND ALIQISS = 0
) WHERE NROOCORENCIA = MAXNROOCORENCIA