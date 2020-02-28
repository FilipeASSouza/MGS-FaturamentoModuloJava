SELECT CODSITE FROM MGSTCTCONTRCENT
WHERE NUMMCONTRATO = :NUMCONTRATO
AND (TRUNC(DTFIM,'MONTH') >= TRUNC(:DTREF,'MONTH') OR DTFIM IS NULL)
AND (CODSITE >= :CODSITEI OR :CODSITEI = 0)
AND (CODSITE <= :CODSITEF OR :CODSITEF = 0)