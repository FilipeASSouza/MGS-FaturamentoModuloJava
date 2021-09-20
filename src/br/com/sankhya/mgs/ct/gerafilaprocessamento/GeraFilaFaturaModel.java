package br.com.sankhya.mgs.ct.gerafilaprocessamento;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel.GeraFila;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GeraFilaFaturaModel {
    private Timestamp dataReferencia;
    private Timestamp dataEmissao;
    private Timestamp dataVencimento;
    private Timestamp dataCusto;
    private BigDecimal codigoTipoFatura;
    private BigDecimal codigoUnidadeFaturamentoInicial;
    private BigDecimal codigoUnidadeFaturamentoFinal;
    private String aprovadas;
    private GeraFilaFactory geraFilaFactory;
    private JdbcWrapper jdbcWrapper;
    NativeSqlDecorator consultaListaCodigoSites;
    
    public GeraFilaFaturaModel(JdbcWrapper jdbc) throws Exception {
        this.jdbcWrapper = jdbc;
        geraFilaFactory = new GeraFilaFactory(jdbcWrapper);
        consultaListaCodigoSites = new NativeSqlDecorator(this, "BuscaListaUnidadeFaturamentoFaturaAprovadas.sql",this.jdbcWrapper);
    }
    
    public void setDataReferencia(Timestamp dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public void setDataEmissao(Timestamp dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public void setDataVencimento(Timestamp dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public void setDataCusto(Timestamp dataCusto) {
        this.dataCusto = dataCusto;
    }

    public void setCodigoTipoFatura(BigDecimal codigoTipoFatura) {
        this.codigoTipoFatura = codigoTipoFatura;
    }

    public void setCodigoUnidadeFaturamentoInicial(BigDecimal codigoUnidadeFaturamentoInicial) {
        this.codigoUnidadeFaturamentoInicial = codigoUnidadeFaturamentoInicial;
    }

    public void setCodigoUnidadeFaturamentoFinal(BigDecimal codigoUnidadeFaturamentoFinal) {
        this.codigoUnidadeFaturamentoFinal = codigoUnidadeFaturamentoFinal;
    }

    public void setAprovadas(String aprovadas) {
        this.aprovadas = aprovadas;
    }
    public void gerarFila() throws Exception {
    
    
        consultaListaCodigoSites.cleanParameters();
        consultaListaCodigoSites.setParametro("CODSITEI", codigoUnidadeFaturamentoInicial);
        consultaListaCodigoSites.setParametro("CODSITEF", codigoUnidadeFaturamentoFinal);
        consultaListaCodigoSites.setParametro("CODTIPOFATURA", codigoTipoFatura);
        consultaListaCodigoSites.setParametro("DTLANCCUSTO", dataCusto);



        while (consultaListaCodigoSites.proximo()) {
            BigDecimal codigoUnidadeFaturamento = consultaListaCodigoSites.getValorBigDecimal("CODSITE");
            gerarFilaPorUnidadeFaturamento(codigoUnidadeFaturamento);
        }


    }

    public void gerarFilaAprovados() throws Exception {
    
        consultaListaCodigoSites.cleanParameters();
        consultaListaCodigoSites.setParametro("CODSITEI", codigoUnidadeFaturamentoInicial);
        consultaListaCodigoSites.setParametro("CODSITEF", codigoUnidadeFaturamentoFinal);
        consultaListaCodigoSites.setParametro("CODTIPOFATURA", codigoTipoFatura);
        consultaListaCodigoSites.setParametro("DTLANCCUSTO", dataCusto);



        while (consultaListaCodigoSites.proximo()) {
            BigDecimal codigoUnidadeFaturamento = consultaListaCodigoSites.getValorBigDecimal("CODSITE");
            gerarFilaPorUnidadeFaturamento(codigoUnidadeFaturamento);
        }

    }

    private void gerarFilaPorUnidadeFaturamento(BigDecimal codigoUnidadeFaturamento) throws Exception {
        GeraFila geraFila = geraFilaFactory.getGeraFila("CONTR_INS_LANC_FATURA");
        if (geraFila != null) {

            geraFila.setParametroExecucao("dataReferencia",dataReferencia);
            geraFila.setParametroExecucao("dataEmissao",dataEmissao);
            geraFila.setParametroExecucao("dataVencimento",dataVencimento);
            geraFila.setParametroExecucao("dataCusto",dataCusto);
            geraFila.setParametroExecucao("codigoTipoFatura",codigoTipoFatura);
            geraFila.setParametroExecucao("codigoUnidadeFaturamento",codigoUnidadeFaturamento);
            geraFila.setParametroExecucao("aprovadas",aprovadas);
            geraFila.executar();
        }
    }

}
