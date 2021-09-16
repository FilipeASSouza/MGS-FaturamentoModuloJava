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

public class IntegracaoDetalhaCustoModel {
    static private JapeWrapper integracaoDetalhaCustoDAO = JapeFactory.dao("MGSCT_Integr_Detalha_Custo");
    private BigDecimal numeroUnicoIntegracao = BigDecimal.ZERO;

    public IntegracaoDetalhaCustoModel() {

    }

    static public void atualizaComplemento(BigDecimal numeroUnicoIntegracao, String complemento) throws Exception {
        NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator("UPDATE MGSTCTINTEGRADC SET COMPLEMENTO = :COMPLEMENTO WHERE NUINTEGRADC = :NUINTEGRADC");
        nativeSqlDecorator.setParametro("NUINTEGRADC",numeroUnicoIntegracao);
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
        DynamicVO dynamicVO = integracaoDetalhaCustoDAO.findOne("NUMCONTRATO = ? AND  CODUNIDADEFATUR = ? AND INTPERIODO = ? AND INTCOMPETENCIA = ? AND CODORIGEM = ? AND DHDESFAZER IS NULL",
                i.getNumeroContrato(),
                i.getCodigpUnidadeFaturamento(),
                i.getCodigoPeriodo(),
                i.getCodigoCompetencia(),
                i.getCodigoOrigem()
        );

        if (dynamicVO != null) {
            this.numeroUnicoIntegracao = dynamicVO.asBigDecimal("NUINTEGRADC");
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
                    caller.addInputParameter("MGSTCTINTEGRADC");//P_ARQUIVO IN VARCHAR,
                    caller.addInputParameter("1");//P_CODEMP IN INT,
                    caller.addInputParameter("MGSTCTINTEGRADC");//P_TABELA IN VARCHAR,
                    caller.addInputParameter("NUINTEGRADC");//P_CAMPO IN VARCHAR,
                    caller.addInputParameter("0");//P_DSYNC IN INT,
                    caller.addOutputParameter(2, "P_ULTCOD");//P_ULTCOD OUT NUMBER
                    caller.execute(jdbc.getConnection());
                    this.numeroUnicoIntegracao = caller.resultAsBigDecimal("P_ULTCOD");

                    NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator(this, "InsereDetalhamentoCusto.sql");

                    nativeSqlDecorator.setParametro("NUINTEGRADC", this.numeroUnicoIntegracao);
                    nativeSqlDecorator.setParametro("NUMCONTRATO", i.getNumeroContrato());
                    nativeSqlDecorator.setParametro("CODUNIDADEFATUR", i.getCodigpUnidadeFaturamento());
                    nativeSqlDecorator.setParametro("INTPERIODO", i.getCodigoPeriodo());
                    nativeSqlDecorator.setParametro("INTCOMPETENCIA", i.getCodigoCompetencia());
                    nativeSqlDecorator.setParametro("CODORIGEM", i.getCodigoOrigem());
                    nativeSqlDecorator.setParametro("DHINS", TimeUtils.getNow());
                    nativeSqlDecorator.setParametro("USUINS", i.getCodigoUsuarioInsercao());
                    nativeSqlDecorator.atualizar();
                }
            } finally {
                //JapeSession.close(hnd);
                //JdbcWrapper.closeSession(jdbc);
            }
            return true;

    }

    public class IntegracaoDetalhaCustoPOJO {
        private BigDecimal numeroContrato;
        private BigDecimal codigpUnidadeFaturamento;
        private BigDecimal codigoPeriodo;
        private BigDecimal codigoCompetencia;
        private BigDecimal codigoOrigem;
        private BigDecimal codigoUsuarioInsercao;

        public BigDecimal getNumeroContrato() {
            return numeroContrato;
        }

        public void setNumeroContrato(BigDecimal numeroContrato) {
            this.numeroContrato = numeroContrato;
        }

        public BigDecimal getCodigpUnidadeFaturamento() {
            return codigpUnidadeFaturamento;
        }

        public void setCodigpUnidadeFaturamento(BigDecimal codigpUnidadeFaturamento) {
            this.codigpUnidadeFaturamento = codigpUnidadeFaturamento;
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

        public BigDecimal getCodigoOrigem() {
            return codigoOrigem;
        }

        public void setCodigoOrigem(BigDecimal codigoOrigem) {
            this.codigoOrigem = codigoOrigem;
        }

        public BigDecimal getCodigoUsuarioInsercao() {
            return codigoUsuarioInsercao;
        }

        public void setCodigoUsuarioInsercao(BigDecimal codigoUsuarioInsercao) {
            this.codigoUsuarioInsercao = codigoUsuarioInsercao;
        }
    }
}
