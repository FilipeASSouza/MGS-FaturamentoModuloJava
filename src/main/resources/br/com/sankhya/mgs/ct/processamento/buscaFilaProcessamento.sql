SELECT NUFILAPROC, NUTIPOPROC, CHAVE
FROM MGSTCTFILAPROC
WHERE STATUS = 'I'
AND ROWNUM <= :QTDEXECFILA
AND NUTIPOPROC NOT IN( 13, 15, 16, 17, 22, 24, 25 )