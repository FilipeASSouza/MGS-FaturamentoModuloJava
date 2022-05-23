SELECT MGSTCTCONTRCENT.CODSITE FROM MGSTCTCONTRCENT
inner join mgstctlocalcont on mgstctlocalcont.nulocalcont = mgstctcontrcent.nulocalcont
WHERE nvl(Mgstctcontrcent.Dtfim,sysdate) >= sysdate
and MGSTCTCONTRCENT.NUMCONTRATO = :NUMCONTRATO
and mgstctlocalcont.numodalidade = :NUMODALIDADE
AND (CODSITE >= :CODSITEI OR :CODSITEI = 0)
AND (CODSITE <= :CODSITEF OR :CODSITEF = 0)
