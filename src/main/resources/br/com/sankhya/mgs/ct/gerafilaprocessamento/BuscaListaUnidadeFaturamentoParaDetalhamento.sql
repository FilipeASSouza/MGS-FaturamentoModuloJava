SELECT MGSTCTCONTRCENT.CODSITE FROM MGSTCTCONTRCENT
inner join mgstctlocalcont on mgstctlocalcont.nulocalcont = mgstctcontrcent.nulocalcont
WHERE nvl(Mgstctcontrcent.Dtfim,sysdate) >= sysdate
and MGSTCTCONTRCENT.NUMCONTRATO = :NUMCONTRATO
/*
and mgstctlocalcont.numodalidade = :NUMODALIDADE
Motivo: ALTERADO DEVIDO AO GERAR O DETALHAMENTO PARA TODAS AS MODALIDADES
23/07/2021
 */
  AND (CODSITE >= :CODSITEI OR :CODSITEI = 0)
  AND (CODSITE <= :CODSITEF OR :CODSITEF = 0)
