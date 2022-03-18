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
    private BigDecimal codigoUnidadeFaturamentoFinal;
    private GeraFilaFactory geraFilaFactory = new GeraFilaFactory();

    //Verificar a Fila de Processamento

    private BigDecimal numeroContratoVerificacao;
    private BigDecimal dataContratoVerificacao;

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

    public void setCodigoUnidadeFaturamentoFinal(BigDecimal codigoUnidadeFaturamentoFinal){
        this.codigoUnidadeFaturamentoFinal = codigoUnidadeFaturamentoFinal;
    }

    public void gerarFila() throws Exception {

        aprovaRelatorioFiscal(); // fiscal

        gerarTabelaTemporaria(); // anexocad

        NativeSqlDecorator consultaListaCodigoSites = new NativeSqlDecorator(this,"GeraFilaLancamentoCustoGestorConsulta.sql");
        consultaListaCodigoSites.setParametro("CODUNIDADEFATUR",this.codigoUnidadeFaturamento);
        consultaListaCodigoSites.setParametro("CODUNIDADEFATURFIN", this.codigoUnidadeFaturamentoFinal);

        while (consultaListaCodigoSites.proximo()) {

            Timestamp dataCusto = consultaListaCodigoSites.getValorTimestamp("MESRELATORIO");
            BigDecimal codigoUnidadeFaturamento = consultaListaCodigoSites.getValorBigDecimal("CODUNIDADEFATUR");
            BigDecimal codigoTipoFatura = consultaListaCodigoSites.getValorBigDecimal("CODTIPOFATURA");
            numeroContrato = consultaListaCodigoSites.getValorBigDecimal("NUMCONTRATO");

            /*
            Comentado para testar a performance na inserção da fila quando está ativo a inserção na fila de processamento fica extremamente lenta
            Necessário verificar se é preciso que seja feito esse teste nesse ponto ou implementar em outro passo do projeto

            NativeSqlDecorator verificarFilaProcessamento = new NativeSqlDecorator(this, "VerificarFilaProcessamento.sql" );
            verificarFilaProcessamento.setParametro("CODUNIDADEFATUR", codigoUnidadeFaturamento );


            while( verificarFilaProcessamento.proximo() ){


                String chave = verificarFilaProcessamento.getValorString("CHAVE");
                String arrayChave[];
                arrayChave = chave.split(";");

                numeroContratoVerificacao = BigDecimal.valueOf(Long.parseLong(arrayChave[2].replace("V_CONTRATO=","")));
                dataContratoVerificacao = verificarFilaProcessamento.getValorBigDecimal("DATA");

                executarVerificacaoFilaProcessamento();
            }*/

            GeraFila geraFila = geraFilaFactory.getGeraFila("CONTR_INS_LANC_CUSTO_UP_GESTOR");
            if ( geraFila != null ) {
                geraFila.setParametroExecucao("numeroUnidadeFaturamento", codigoUnidadeFaturamento );
                geraFila.setParametroExecucao("dataCusto", dataCusto ); // Data Esta sendo convertida dentro da query
                geraFila.setParametroExecucao("numeroContrato", numeroContrato );
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
        caller.addInputParameter(codigoUnidadeFaturamento); //V_CODUNIDADEFATPAI   IN     NUMBER,
        caller.addInputParameter(codigoUnidadeFaturamentoFinal);//V_CODUNIDADEFATPAIFINAL   IN     NUMBER,

        caller.execute(jdbc.getConnection());

        JapeSession.close(hnd);
        JdbcWrapper.closeSession(jdbc);
    }

    private void gerarTabelaTemporaria() throws Exception {
        JapeSession.SessionHandle hnd = JapeSession.open();
        final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbc = dwfFacade.getJdbcWrapper();
        jdbc.openSession();

        ProcedureCaller caller = new ProcedureCaller("CONTR_INS_ANEXO_CAD");

        caller.addInputParameter( numeroContrato );//V_CONTRATO         IN     NUMBER,
        caller.addInputParameter( TimeUtils.formataYYYYMMDD( dataCusto ) );//V_MESFATURAMENTO   IN     NUMBER,
        caller.addInputParameter( codigoUnidadeFaturamento );//V_UNIDADEFAT_PAI   IN     NUMBER,
        caller.addInputParameter( codigoUnidadeFaturamentoFinal );
        caller.addInputParameter( BigDecimal.ZERO );
        caller.addOutputParameter(1, "LOG" );//LOG_ERRO_SQL          OUT VARCHAR2,
        caller.addOutputParameter(2, "SUCESSO" );//V_SUCESSO             OUT NUMBER
        
        caller.execute(jdbc.getConnection());

        String log = "";
        BigDecimal sucesso = null;

        log = caller.resultAsString("LOG");

        sucesso = caller.resultAsBigDecimal("SUCESSO");
        JapeSession.close(hnd);
        JdbcWrapper.closeSession(jdbc);

    }

    public void executarVerificacaoFilaProcessamento() throws Exception{
        JapeSession.SessionHandle hnd = JapeSession.open();
        final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbc = dwfFacade.getJdbcWrapper();
        jdbc.openSession();

        ProcedureCaller caller = new ProcedureCaller("CONTR_INS_ANEXO_CAD");

        caller.addInputParameter( numeroContratoVerificacao );//V_CONTRATO         IN     NUMBER,
        caller.addInputParameter( TimeUtils.formataYYYYMMDD( dataContratoVerificacao ) );//V_MESFATURAMENTO   IN     NUMBER,
        caller.addInputParameter( codigoUnidadeFaturamento );//V_UNIDADEFAT_PAI   IN     NUMBER,
        caller.addInputParameter( codigoUnidadeFaturamentoFinal );//V_UNIDADEFAT_PAI   IN     NUMBER,
        caller.addInputParameter( BigDecimal.ONE );
        caller.addOutputParameter(1, "LOG");//LOG_ERRO_SQL          OUT VARCHAR2,
        caller.addOutputParameter(2, "SUCESSO");//V_SUCESSO             OUT NUMBER

        caller.execute(jdbc.getConnection());

        String log = "";
        BigDecimal sucesso = null;

        log = caller.resultAsString("LOG");

        sucesso = caller.resultAsBigDecimal("SUCESSO");
        JapeSession.close(hnd);
        JdbcWrapper.closeSession(jdbc);
    }
}
