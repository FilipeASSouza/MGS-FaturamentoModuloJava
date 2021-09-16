package br.com.sankhya.mgs.ct.gerafilaprocessamento;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel.GeraFila;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GeraFilaLancamentoCustoModel {
    private Timestamp dataReferencia;
    private Timestamp dataCusto;
    private BigDecimal unidadeFaturamentoInicial;
    private BigDecimal unidadeFaturamentoFinal;
    private BigDecimal numeroContrato;
    private BigDecimal numeroUnicoModalidade;
    private BigDecimal codigoTipoFatura;
    private GeraFilaFactory geraFilaFactory = new GeraFilaFactory();

    public void setDataReferencia(Timestamp dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public void setDataCusto(Timestamp dataCusto) {
        this.dataCusto = dataCusto;
    }

    public void setCodigoTipoFatura(BigDecimal codigoTipoFatura) {
        this.codigoTipoFatura = codigoTipoFatura;
    }

    public void setUnidadeFaturamentoInicial(BigDecimal unidadeFaturamentoInicial) {
        if (unidadeFaturamentoInicial == null) {
            this.unidadeFaturamentoInicial = BigDecimal.ZERO;
        } else {
            this.unidadeFaturamentoInicial = unidadeFaturamentoInicial;
        }

    }

    public void setUnidadeFaturamentoFinal(BigDecimal unidadeFaturamentoFinal) {
        if (unidadeFaturamentoFinal == null) {
            this.unidadeFaturamentoFinal = BigDecimal.ZERO;
        } else {
            this.unidadeFaturamentoFinal = unidadeFaturamentoFinal;
        }
    }

    public void setNumeroContrato(BigDecimal numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public void setNumeroUnicoModalidade(BigDecimal numeroUnicoModalidade) {
        this.numeroUnicoModalidade = numeroUnicoModalidade;
    }

    public void gerarFila() throws Exception {

        NativeSqlDecorator consultaListaCodigoSites = new NativeSqlDecorator(this, "BuscaListaUnidadeFaturamentoParaProcessamento.sql");
        consultaListaCodigoSites.setParametro("CODSITEI", unidadeFaturamentoInicial);
        consultaListaCodigoSites.setParametro("CODSITEF", unidadeFaturamentoFinal);
        consultaListaCodigoSites.setParametro("NUMCONTRATO", numeroContrato);

        while (consultaListaCodigoSites.proximo()) {
            BigDecimal codigoUnidadeFaturamento = consultaListaCodigoSites.getValorBigDecimal("CODSITE");
            gerarFilaPorUnidadeFaturamento(codigoUnidadeFaturamento);
        }


    }

    private void gerarFilaPorUnidadeFaturamento(BigDecimal unidadeFaturamento) throws Exception {
        GeraFila geraFila = geraFilaFactory.getGeraFila("CONTR_INS_LANC_CUSTO_UP");
        if (geraFila != null) {
            geraFila.setParametroExecucao("numeroUnidadeFaturamento", unidadeFaturamento);
            geraFila.setParametroExecucao("dataReferencia", dataReferencia);
            geraFila.setParametroExecucao("dataCusto", dataCusto);
            geraFila.setParametroExecucao("numeroContrato", numeroContrato);
            geraFila.setParametroExecucao("numeroUnicoModalidade", numeroUnicoModalidade);
            geraFila.setParametroExecucao("codigoTipoFatura", codigoTipoFatura);
            geraFila.executar();
        }
    }


}
