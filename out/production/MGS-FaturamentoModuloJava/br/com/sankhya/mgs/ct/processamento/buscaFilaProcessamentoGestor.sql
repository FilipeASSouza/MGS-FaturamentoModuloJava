SELECT NUFILAPROC, NUTIPOPROC, CHAVE
FROM MGSTCTFILAPROC
WHERE STATUS = 'I'
AND ROWNUM <= :QTDEXECFILA
AND NUTIPOPROC IN( 16, 17 )