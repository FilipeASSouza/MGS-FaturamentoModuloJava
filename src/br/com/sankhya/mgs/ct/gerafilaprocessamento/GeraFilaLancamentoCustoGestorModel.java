package br.com.sankhya.mgs.ct.gerafilaprocessamento;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel.GeraFila;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.ProcedureCaller;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GeraFilaLancamentoCustoGestorModel {
    private Timestamp dataCusto;
    private BigDecimal numeroContrato;
    private BigDecimal numeroUnicoModalidade;
    private BigDecimal codigoUnidadeFaturamento;
    private GeraFilaFactory geraFilaFactory = new GeraFilaFactory();

    public void setDataCusto(Timestamp dataCusto) {
        this.dataCusto = dataCusto;
    }

    public void setNumeroContrato(BigDecimal numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public void setNumeroUnicoModalidade(BigDecimal numeroUnicoModalidade) {
        this.numeroUnicoModalidade = numeroUnicoModalidade;
    }

    public void setCodigoUnidadeFaturamento(BigDecimal codigoUnidadeFaturamento) {
        this.codigoUnidadeFaturamento = codigoUnidadeFaturamento;
    }

    public void gerarFila() throws Exception {
        aprovaRelatorioFiscal();

        gerarTabelaTemporaria();

        NativeSqlDecorator consultaListaCodigoSites = new NativeSqlDecorator(this,"GeraFilaLancamentoCustoGestorConsulta.sql");
        while (consultaListaCodigoSites.proximo()) {
            Timestamp dataCusto = consultaListaCodigoSites.getValorTimestamp("MESRELATORIO");
            BigDecimal codigoUnidadeFaturamento = consultaListaCodigoSites.getValorBigDecimal("CODUNIDADEFATUR");
            BigDecimal codigoTipoFatura = consultaListaCodigoSites.getValorBigDecimal("CODTIPOFATURA");
            GeraFila geraFila = geraFilaFactory.getGeraFila("CONTR_INS_LANC_CUSTO_UP_GESTOR");
            if (geraFila != null) {
                geraFila.setParametroExecucao("numeroUnidadeFaturamento", codigoUnidadeFaturamento);
                geraFila.setParametroExecucao("dataCusto", dataCusto);
                geraFila.setParametroExecucao("numeroContrato", numeroContrato);
                geraFila.setParametroExecucao("numeroUnicoModalidade", numeroUnicoModalidade);
                geraFila.setParametroExecucao("codigoTipoFatura", codigoTipoFatura);
                geraFila.executar();
            }
        }
    }

    private void aprovaRelatorioFiscal() throws Exception {//RELATORIO_ANEXOS_APROVA(V_MESFATURAMENTO, V_CONTRATO);
        JapeSession.SessionHandle hnd = JapeSession.open();
        final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbc = dwfFacade.getJdbcWrapper();
        jdbc.openSession();

        ProcedureCaller caller = new ProcedureCaller("RELATORIO_ANEXOS_APROVA");

        caller.addInputParameter(TimeUtils.getYearMonth(dataCusto));//V_MESFATURAMENTO   IN     NUMBER,
        caller.addInputParameter(numeroContrato);//V_CONTRATO         IN     NUMBER

        caller.execute(jdbc.getConnection());
    }

    private void gerarTabelaTemporaria() throws Exception {
        JapeSession.SessionHandle hnd = JapeSession.open();
        final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbc = dwfFacade.getJdbcWrapper();
        jdbc.openSession();

        ProcedureCaller caller = new ProcedureCaller("CONTR_INS_ANEXO_CAD");

        caller.addInputParameter(numeroContrato);//V_CONTRATO         IN     NUMBER,
        caller.addInputParameter(TimeUtils.getYearMonth(dataCusto));//V_MESFATURAMENTO   IN     NUMBER,
        caller.addInputParameter(codigoUnidadeFaturamento);//V_UNIDADEFAT_PAI   IN     NUMBER,
        caller.addOutputParameter(1, "LOG");//LOG_ERRO_SQL          OUT VARCHAR2,
        caller.addOutputParameter(2, "SUCESSO");//V_SUCESSO             OUT NUMBER
        
        caller.execute(jdbc.getConnection());

        String log = "";
        BigDecimal sucesso = null;

        log = caller.resultAsString("LOG");

        sucesso = caller.resultAsBigDecimal("SUCESSO");

    }
}
