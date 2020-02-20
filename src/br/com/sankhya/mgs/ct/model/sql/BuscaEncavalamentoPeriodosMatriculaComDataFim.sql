SELECT   COUNT ( * ) QTD
  FROM   MGSTCTALOCACAOPS
 WHERE   NUALOCAPS <> :NUALOCAPS AND MATRICULA = :MATRICULA
 AND CODEVENTO IN (SELECT CODEVENTO FROM AD_TGFECUS WHERE TIPOPOSTO = :TIPOPOSTO)
         AND (   (TRUNC (:DTI) BETWEEN TRUNC (DTINICIO) AND TRUNC (DTFIM))
              OR (TRUNC (:DTF) BETWEEN TRUNC (DTINICIO) AND TRUNC (DTFIM))
              OR (TRUNC (DTINICIO) BETWEEN TRUNC (:DTI) AND TRUNC (:DTF))
              OR (TRUNC (DTFIM) BETWEEN TRUNC (:DTI) AND TRUNC (:DTF))
              OR (TRUNC (:DTI) >= TRUNC (DTINICIO) AND TRUNC (DTFIM) IS NULL))