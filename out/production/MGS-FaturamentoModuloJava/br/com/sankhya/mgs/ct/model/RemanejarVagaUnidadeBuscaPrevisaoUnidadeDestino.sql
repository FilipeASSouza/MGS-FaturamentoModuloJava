SELECT NUUNIDPREV 
FROM MGSTCTUNIDADEPREV
WHERE NUCONTRCENT = :NUCONTRCENT
AND CODTIPOPOSTO = :CODTIPOPOSTO OR :CODTIPOPOSTO IS NULL
AND CODSERVMATERIAL = :CODSERVMATERIAL OR :CODSERVMATERIAL IS NULL
AND CODEVENTO = :CODEVENTO OR :CODEVENTO IS NULL