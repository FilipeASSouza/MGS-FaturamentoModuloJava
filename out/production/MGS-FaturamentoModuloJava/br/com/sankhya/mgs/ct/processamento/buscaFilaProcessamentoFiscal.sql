SELECT NUFILAPROC, NUTIPOPROC, CHAVE
FROM MGSTCTFILAPROC
WHERE STATUS = 'I'
AND ROWNUM <= :QTDEXECFILAFISC
AND NUTIPOPROC IN( 13, 15 )