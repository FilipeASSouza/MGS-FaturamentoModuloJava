package br.com.sankhya.mgs.ct.gerafilaprocessamento;

import br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel.GeraFila;

import java.math.BigDecimal;

public class GerarFilaLancamentoFaturaPortalModel {
    private BigDecimal numeroFatura;
    private GeraFilaFactory geraFilaFactory = new GeraFilaFactory();

    public void setNumeroFatura(BigDecimal numeroFatura) {
        this.numeroFatura = numeroFatura;
    }

    public void gerarFila() throws Exception {
        GeraFila geraFila = geraFilaFactory.getGeraFila("CONTR_INS_LANC_FATURA_PORTAL");
        if (geraFila != null) {

            geraFila.setParametroExecucao("numeroFatura",this.numeroFatura);

            geraFila.executar();
        }
    }
}
