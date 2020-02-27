SELECT   COUNT ( * ) QTD
  FROM   MGSTCTALOCACAOPS
 WHERE       NUALOCAPS <> :NUALOCAPS
         AND MATRICULA = :MATRICULA
         AND CODEVENTO IN (SELECT CODEVENTO FROM AD_TGFECUS WHERE TIPOPOSTO = :TIPOPOSTO)
         AND ( (TRUNC (:DTI) >= TRUNC (DTINICIO) AND TRUNC (DTFIM) IS NULL) OR TRUNC(DTINICIO) > TRUNC (:DTI))