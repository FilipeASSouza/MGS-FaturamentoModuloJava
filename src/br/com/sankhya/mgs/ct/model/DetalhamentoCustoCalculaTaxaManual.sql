SELECT
:VALOR_DIGITADO + (:VALOR_DIGITADO * ((SUBSTR(NVL( BUSCA_TAXA_INCIDENCIA(:UNIDADEFATURAMENTO,:CODTIPOFATURA,'F'), 0 ),1,7)*1 ) /100 ))
AS VALOR
FROM DUAL

