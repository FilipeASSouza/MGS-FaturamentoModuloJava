package br.com.sankhya.mgs.ct.gerafilaprocessamento;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel.GeraFila;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;

public class GeraFilaModel {
    private Timestamp dataReferencia;
    private BigDecimal unidadeFaturamentoInicial;
    private BigDecimal unidadeFaturamentoFinal;
    private BigDecimal numeroContrato;
    private GeraFilaFactory geraFilaFactory = new GeraFilaFactory();

    public void setDataReferencia(Timestamp dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public void setUnidadeFaturamentoInicial(BigDecimal unidadeFaturamentoInicial) {
        if (unidadeFaturamentoInicial == null) {
            this.unidadeFaturamentoInicial = BigDecimal.ZERO;
        } else {
            this.unidadeFaturamentoInicial = unidadeFaturamentoInicial;
        }

    }

    public void setUnidadeFaturamentoFinal(BigDecimal unidadeFaturamentoFinal) {
        if (unidadeFaturamentoFinal == null){
            this.unidadeFaturamentoFinal = BigDecimal.ZERO;
        } else {
            this.unidadeFaturamentoFinal = unidadeFaturamentoFinal;
        }
    }

    public void setNumeroContrato(BigDecimal numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public void gerarFila() throws Exception {

        NativeSqlDecorator consultaListaCodigoSites = new NativeSqlDecorator(this,"BuscaListaUnidadeFaturamentoParaProcessamento.sql");
        consultaListaCodigoSites.setParametro("CODSITEI",unidadeFaturamentoInicial);
        consultaListaCodigoSites.setParametro("CODSITEF",unidadeFaturamentoFinal);
        consultaListaCodigoSites.setParametro("NUMCONTRATO",numeroContrato);

        while(consultaListaCodigoSites.proximo()){
            BigDecimal codigoUnidadeFaturamento = consultaListaCodigoSites.getValorBigDecimal("CODSITE");
            gerarFilaPorUnidadeFaturamento(codigoUnidadeFaturamento);
        }





    }

    private void gerarFilaPorUnidadeFaturamento(BigDecimal unidadeFaturamento) throws Exception {
        Collection<DynamicVO> metricasContratoVOS = JapeFactory.dao("MGSCT_Metricas").find("NUMCONTRATO = ?", numeroContrato);
        for (DynamicVO metricasContratoVO : metricasContratoVOS){
            GeraFila geraFila = geraFilaFactory.getGeraFila(metricasContratoVO.asBigDecimal("NUTIPOMETRICA"));
            if (geraFila != null) {
                geraFila.setParametroExecucao("NumeroUnicoMetrica",metricasContratoVO.asBigDecimal("NUCONTRMETRICA"));
                geraFila.setParametroExecucao("NumeroUnidadeFaturamento",unidadeFaturamento);
                geraFila.setParametroExecucao("DataReferencia",dataReferencia);
                geraFila.setParametroExecucao("NumeroContrato",numeroContrato);
                geraFila.executar();
            }
        }
    }
}
