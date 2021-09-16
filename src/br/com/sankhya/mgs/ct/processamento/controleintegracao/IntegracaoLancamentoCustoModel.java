package br.com.sankhya.mgs.ct.processamento.controleintegracao;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.ProcedureCaller;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class IntegracaoLancamentoCustoModel {
    static private JapeWrapper integracaoLancamentoCustoDAO = JapeFactory.dao("MGSCT_Integr_Lanc_Custo");
    private BigDecimal numeroUnicoIntegracao = BigDecimal.ZERO;
    NativeSqlDecorator nativeSqlDecorator;
    NativeSqlDecorator nativeSqlInsereLancamentoCusto;
    private JdbcWrapper jdbcWrapper;
    public IntegracaoLancamentoCustoModel(JdbcWrapper jdbc) throws Exception {
        this.jdbcWrapper  = jdbc;
        nativeSqlDecorator = new NativeSqlDecorator("UPDATE MGSTCTINTEGRALC SET COMPLEMENTO = :COMPLEMENTO WHERE NUINTEGRALC = :NUINTEGRALC",jdbcWrapper);
        nativeSqlInsereLancamentoCusto = new NativeSqlDecorator(this, "InsereLancamentoCusto.sql",this.jdbcWrapper);
    }

    public void atualizaComplemento(BigDecimal numeroUnicoIntegracao, String complemento) throws Exception {
        nativeSqlDecorator.cleanParameters();
        nativeSqlDecorator.setParametro("NUINTEGRALC",numeroUnicoIntegracao);
        nativeSqlDecorator.setParametro("COMPLEMENTO",complemento);
        nativeSqlDecorator.atualizar();
    }

    public BigDecimal getNumeroUnicoIntegracao() {
        return numeroUnicoIntegracao;
    }

    public IntegracaoDetalhaCustoPOJO getPojo() {
        IntegracaoDetalhaCustoPOJO integracaoDetalhaCustoPOJO = new IntegracaoDetalhaCustoPOJO();
        return integracaoDetalhaCustoPOJO;
    }

    public boolean salvarIntegracao(IntegracaoDetalhaCustoPOJO i) throws Exception {
        DynamicVO dynamicVO = integracaoLancamentoCustoDAO.findOne("NUMODALIDADE = ? AND NUMCONTRATO = ? AND CODTIPOFATURA = ? AND CODUNIDADEFATUR = ? AND DTLANCCUSTO = ? AND INTPERIODO = ? AND INTCOMPETENCIA = ? AND TIPGESTOR = 'F' AND DHDESFAZER IS NULL",
                i.getNumeroUnicoModalidade(),//NUMODALIDADE
                i.getNumeroContrato(),//NUMCONTRATO
                i.getCodigoTipoFatura(),//CODTIPOFATURA
                i.getCodigoUnidadeFaturamento(),//CODUNIDADEFATUR
                i.getDataCusto(),//DTLANCCUSTO
                i.getCodigoPeriodo(),//INTPERIODO
                i.getCodigoCompetencia()//INTCOMPETENCIA
        );

        if (dynamicVO != null) {
            this.numeroUnicoIntegracao = dynamicVO.asBigDecimal("NUINTEGRALC");
            if("S".equals(dynamicVO.asString("COMPLEMENTO"))){
                return false;
            }
        }


            JdbcWrapper jdbc = null;
            JapeSession.SessionHandle hnd = null;
            try {
                hnd = JapeSession.open();
                final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
                jdbc = dwfFacade.getJdbcWrapper();
                jdbc.openSession();

                if (BigDecimal.ZERO.equals(this.numeroUnicoIntegracao)) {
                    ProcedureCaller caller = new ProcedureCaller("STP_KEYGEN_TGFNUM");
                    caller.addInputParameter("MGSTCTINTEGRALC");//P_ARQUIVO IN VARCHAR,
                    caller.addInputParameter("1");//P_CODEMP IN INT,
                    caller.addInputParameter("MGSTCTINTEGRALC");//P_TABELA IN VARCHAR,
                    caller.addInputParameter("NUINTEGRALC");//P_CAMPO IN VARCHAR,
                    caller.addInputParameter("0");//P_DSYNC IN INT,
                    caller.addOutputParameter(2, "P_ULTCOD");//P_ULTCOD OUT NUMBER
                    caller.execute(jdbc.getConnection());
                    this.numeroUnicoIntegracao = caller.resultAsBigDecimal("P_ULTCOD");

                    
                    nativeSqlInsereLancamentoCusto.setParametro("NUINTEGRALC", this.numeroUnicoIntegracao);
                    nativeSqlInsereLancamentoCusto.setParametro("NUMCONTRATO", i.getNumeroContrato());
                    nativeSqlInsereLancamentoCusto.setParametro("CODUNIDADEFATUR", i.getCodigoUnidadeFaturamento());
                    nativeSqlInsereLancamentoCusto.setParametro("INTPERIODO", i.getCodigoPeriodo());
                    nativeSqlInsereLancamentoCusto.setParametro("INTCOMPETENCIA", i.getCodigoCompetencia());
                    nativeSqlInsereLancamentoCusto.setParametro("NUMODALIDADE", i.getNumeroUnicoModalidade());
                    nativeSqlInsereLancamentoCusto.setParametro("CODTIPOFATURA", i.codigoTipoFatura);
                    nativeSqlInsereLancamentoCusto.setParametro("DTLANCCUSTO", i.getDataCusto());
                    nativeSqlInsereLancamentoCusto.setParametro("TIPGESTOR", "F");
                    nativeSqlInsereLancamentoCusto.setParametro("DHINS", TimeUtils.getNow());
                    nativeSqlInsereLancamentoCusto.setParametro("USUINS", i.getCodigoUsuarioInsercao());
                    nativeSqlInsereLancamentoCusto.atualizar();
                }
            } finally {
                JapeSession.close(hnd);
                JdbcWrapper.closeSession(jdbc);
            }
            return true;

    }

    public class IntegracaoDetalhaCustoPOJO {
        private BigDecimal numeroContrato;
        private BigDecimal numeroUnicoModalidade;
        private BigDecimal codigoUnidadeFaturamento;
        private BigDecimal codigoTipoFatura;
        private BigDecimal codigoPeriodo;
        private BigDecimal codigoCompetencia;
        private Timestamp dataCusto;
        private BigDecimal codigoUsuarioInsercao;

        public BigDecimal getNumeroContrato() {
            return numeroContrato;
        }

        public void setNumeroContrato(BigDecimal numeroContrato) {
            this.numeroContrato = numeroContrato;
        }

        public BigDecimal getNumeroUnicoModalidade() {
            return numeroUnicoModalidade;
        }

        public void setNumeroUnicoModalidade(BigDecimal numeroUnicoModalidade) {
            this.numeroUnicoModalidade = numeroUnicoModalidade;
        }

        public BigDecimal getCodigoUnidadeFaturamento() {
            return codigoUnidadeFaturamento;
        }

        public void setCodigoUnidadeFaturamento(BigDecimal codigoUnidadeFaturamento) {
            this.codigoUnidadeFaturamento = codigoUnidadeFaturamento;
        }

        public BigDecimal getCodigoTipoFatura() {
            return codigoTipoFatura;
        }

        public void setCodigoTipoFatura(BigDecimal codigoTipoFatura) {
            this.codigoTipoFatura = codigoTipoFatura;
        }

        public BigDecimal getCodigoPeriodo() {
            return codigoPeriodo;
        }

        public void setCodigoPeriodo(BigDecimal codigoPeriodo) {
            this.codigoPeriodo = codigoPeriodo;
        }

        public BigDecimal getCodigoCompetencia() {
            return codigoCompetencia;
        }

        public void setCodigoCompetencia(BigDecimal codigoCompetencia) {
            this.codigoCompetencia = codigoCompetencia;
        }

        public Timestamp getDataCusto() {
            return dataCusto;
        }

        public void setDataCusto(Timestamp dataCusto) {
            this.dataCusto = dataCusto;
        }

        public BigDecimal getCodigoUsuarioInsercao() {
            return codigoUsuarioInsercao;
        }

        public void setCodigoUsuarioInsercao(BigDecimal codigoUsuarioInsercao) {
            this.codigoUsuarioInsercao = codigoUsuarioInsercao;
        }
    }
}
