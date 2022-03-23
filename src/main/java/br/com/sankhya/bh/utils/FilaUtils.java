package br.com.sankhya.bh.utils;

import br.com.sankhya.jape.dao.JdbcWrapper;

import java.math.BigDecimal;

public class FilaUtils {
    private NativeSqlDecorator atualizandoProcessamentoSQL;
    private JdbcWrapper jdbcWrapper;

    public FilaUtils(JdbcWrapper jdbc) {
        this.jdbcWrapper = jdbc;
        atualizandoProcessamentoSQL = new NativeSqlDecorator("UPDATE MGSTCTFILAPROC SET STATUS = :STATUS WHERE NUFILAPROC = :NUFILAPROC", this.jdbcWrapper);
    }

    public void atualizarStatusFila(BigDecimal numeroUnicoFilaProcessamento, String status) throws Exception {

        atualizandoProcessamentoSQL.setParametro("STATUS", status);
        atualizandoProcessamentoSQL.setParametro("NUFILAPROC", numeroUnicoFilaProcessamento);
        atualizandoProcessamentoSQL.atualizar();
    }
}
