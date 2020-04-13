package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.ProcedureCaller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;


public class AplicarPercGlosaModel {
    private JapeSession.SessionHandle hnd = null;
    private JdbcWrapper jdbc = null;
    private String mensagem;
    private ProcedureCaller caller;
    private String nomeProcedure;
    private Boolean sucesso;

private BigDecimal numeroUnicoEventoMensal;
    private BigDecimal competencia;
    private Timestamp dataLancamentoCusto;
    private BigDecimal codigoTipoPosto;
    private BigDecimal codigoServicosMaterial;
    private BigDecimal codigoEvento;
    private BigDecimal percTotEvento;
    private BigDecimal percTxAdm;

    public void executar() throws Exception {
        nomeProcedure = "CONTR_INS_LANC_GLOSA ";
        inicializarExecutar();
        DynamicVO usuario = JapeFactory.dao("Usuario").findByPK(AuthenticationInfo.getCurrent().getUserID());
        String nomeusu = usuario.asString("NOMEUSU");

        caller.addInputParameter(competencia);//v_competenciafat IN NUMBER,
        caller.addInputParameter(dataLancamentoCusto);//v_dtlccusto      IN DATE,
        caller.addInputParameter(codigoTipoPosto);//v_codtipoposto in number,
        caller.addInputParameter(codigoServicosMaterial);//v_codservmaterial in number,
        caller.addInputParameter(codigoEvento);//v_codevento      IN NUMBER,
        caller.addInputParameter(percTotEvento);//v_PERCtotevento   IN NUMBER,
        caller.addInputParameter(percTxAdm);//v_PERCtxadm       IN NUMBER,
        caller.addInputParameter(numeroUnicoEventoMensal);//v_nuevtmensal    IN NUMBER,
        caller.addInputParameter(nomeusu);//V_LOGIN           IN VARCHAR2,
        caller.addOutputParameter(2, "RET");//RET OUT NUMBER

        BigDecimal retorno = null;
        retorno = caller.resultAsBigDecimal("RET");

        sucesso = BigDecimal.ONE.equals(retorno);

        finalizaExecutar();
    }



    protected void inicializarExecutar() throws SQLException {
        hnd = JapeSession.open();
        final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        jdbc = dwfFacade.getJdbcWrapper();
        jdbc.openSession();
        caller = new ProcedureCaller(nomeProcedure);

    }

    protected void finalizaExecutar(){

    }

    public Boolean getSucesso() {
        return sucesso;
    }

    public void setCompetencia(BigDecimal competencia) {
        this.competencia = competencia;
    }

    public void setDataLancamentoCusto(Timestamp dataLancamentoCusto) {
        this.dataLancamentoCusto = dataLancamentoCusto;
    }

    public void setCodigoTipoPosto(BigDecimal codigoTipoPosto) {
        this.codigoTipoPosto = codigoTipoPosto;
    }

    public void setCodigoServicosMaterial(BigDecimal codigoServicosMaterial) {
        this.codigoServicosMaterial = codigoServicosMaterial;
    }

    public void setCodigoEvento(BigDecimal codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public void setPercTotEvento(BigDecimal percTotEvento) {
        this.percTotEvento = percTotEvento;
    }

    public void setPercTxAdm(BigDecimal percTxAdm) {
        this.percTxAdm = percTxAdm;
    }

    public void setNumeroUnicoEventoMensal(BigDecimal numeroUnicoEventoMensal) {
        this.numeroUnicoEventoMensal = numeroUnicoEventoMensal;
    }
}
