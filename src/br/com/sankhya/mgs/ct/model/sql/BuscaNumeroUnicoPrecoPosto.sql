SELECT NUCONTREVENTO FROM (
SELECT NUCONTREVENTO, NROOCORENCIA, MAX(NROOCORENCIA) OVER() AS MAXNROOCORENCIA
FROM MGSTCTCONTRVLRPS
WHERE NUMCONTRATO = :NUMCONTRATO
AND CODTPN = :CODTPN
AND CODTIPOPOSTO = :CODTIPOPOSTO
AND CODEVENTO = :CODEVENTO
AND TRUNC(SYSDATE) BETWEEN DTINICIO AND DTFIM
AND ALIQISS = 0
) WHERE NROOCORENCIA = MAXNROOCORENCIA