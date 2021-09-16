package br.com.sankhya.bh.utils;

import java.math.BigDecimal;

public class FilaUtils {
    public static void atualizarStatusFila(BigDecimal numeroUnicoFilaProcessamento,String status) throws Exception {
        NativeSqlDecorator atualizandoProcessamentoSQL = new NativeSqlDecorator("UPDATE MGSTCTFILAPROC SET STATUS = :STATUS WHERE NUFILAPROC = :NUFILAPROC");
        atualizandoProcessamentoSQL.setParametro("STATUS", status);
        atualizandoProcessamentoSQL.setParametro("NUFILAPROC", numeroUnicoFilaProcessamento);
        atualizandoProcessamentoSQL.atualizar();
    }
}
