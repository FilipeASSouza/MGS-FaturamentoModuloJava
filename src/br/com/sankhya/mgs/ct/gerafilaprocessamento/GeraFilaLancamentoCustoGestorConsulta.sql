SELECT DISTINCT TRUNC(MESRELATORIO) AS MESRELATORIO, CODUNIDADEFATUR, CODTIPOFATURA
FROM MGSTCTRLTANEXOCAD
WHERE CODUNIDADEFATUR = :CODUNIDADEFATUR OR :CODUNIDADEFATUR = 0