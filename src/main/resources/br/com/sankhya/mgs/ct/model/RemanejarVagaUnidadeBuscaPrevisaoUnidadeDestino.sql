SELECT NUUNIDPREV
FROM MGSTCTUNIDADEPREV
WHERE NUCONTRCENT = :NUCONTRCENT
AND (CODTIPOPOSTO = :CODTIPOPOSTO OR :CODTIPOPOSTO IS NULL)
AND (nvl(CODSERVMATERIAL,0) = :CODSERVMATERIAL OR :CODSERVMATERIAL IS NULL)
AND (CODEVENTO = :CODEVENTO OR :CODEVENTO IS NULL)
