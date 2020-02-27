SELECT   COUNT ( * ) QTD
  FROM   MGSTCTALOCACAOPS
 WHERE       NUALOCAPS <> :NUALOCAPS
         AND CODVAGA = :CODVAGA
         AND ( (TRUNC (:DTI) >= TRUNC (DTINICIO) AND TRUNC (DTFIM) IS NULL) OR TRUNC(DTINICIO) > TRUNC (:DTI))