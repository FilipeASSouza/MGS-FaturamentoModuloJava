UPDATE   MGSTCTEVTMENSAL
   SET   TIPLANCEVENTO = 'M', CODINTEGRACAODC = NULL,
         CODSITLANC = :V_SIT,
         DTALTERLANC = SYSDATE,
         CODUSUALTERLANC = :LOGIN
 WHERE   NUEVTMENSAL = :V_NUEVTMENSAL AND CODINTEGRACAOLC IS NULL