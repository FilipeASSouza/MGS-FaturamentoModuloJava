UPDATE MGSTCTFILAPROC
SET
LOGEXEC = SUBSTR(:LOGEXEC,1,4000),
STATUS = :STATUS,
DHPROC = SYSDATE
WHERE NUFILAPROC = :NUFILAPROC