package br.com.sankhya.mgs.ct.gerafilaprocessamento;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel.GeraFila;

import java.math.BigDecimal;

public class GerarFilaLancamentoFaturaPortalModel {
    private BigDecimal numeroFatura;
    private GeraFilaFactory geraFilaFactory;
    private final JdbcWrapper jdbcWrapper;

    public GerarFilaLancamentoFaturaPortalModel(JdbcWrapper jdbc) throws Exception {
        this.jdbcWrapper = jdbc;
        geraFilaFactory = new GeraFilaFactory(jdbcWrapper);

    }

    public void setNumeroFatura(BigDecimal numeroFatura) {
        this.numeroFatura = numeroFatura;
    }

    public void gerarFila() throws Exception {
        GeraFila geraFila = geraFilaFactory.getGeraFila("CONTR_INS_LANC_FATURA_PORTAL");
        if (geraFila != null) {

            geraFila.setParametroExecucao("numeroFatura", this.numeroFatura);

            geraFila.executar();
        }
    }
}
