package br.com.sankhya.mgs.ct.agenda.processamentomodel;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.ProcedureCaller;

import java.math.BigDecimal;
import java.util.Map;

public class prcContrInsCargaEvtM001 extends ProcessarSuper implements Processar {
    private BigDecimal numeroUnicoFilaProcessamento;
    private String mensagem;

    public prcContrInsCargaEvtM001() {
        super();
    }

    public String getMensagem() {
        return mensagem;
    }

    @Override
    public boolean executar() throws Exception {
        JapeSession.SessionHandle hnd = null;
        JdbcWrapper jdbc = null;

        Boolean executado = false;

        try {

            super.executar();


            Map<String, String> parametrosExecutacao = this.getParametrosExecutacao();


            hnd = JapeSession.open();
            final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            jdbc = dwfFacade.getJdbcWrapper();
            jdbc.openSession();


            ProcedureCaller caller = new ProcedureCaller("CONTR_INS_CARGA_EVT_M_001");

            String log = "";
            BigDecimal sucesso = null;

            caller.addInputParameter(parametrosExecutacao.get("V_CONTRATO"));//V_CONTRATO       IN NUMBER,
            caller.addInputParameter(null);//VTP_VAGA         IN VARCHAR2,
            caller.addInputParameter(parametrosExecutacao.get("V_TP_APONTAMENTO"));//V_TP_APONTAMENTO IN VARCHAR2,
            caller.addInputParameter(parametrosExecutacao.get("MES_CARGA"));//MES_CARGA        IN NUMBER,
            caller.addInputParameter(parametrosExecutacao.get("MES_FAT"));//MES_FAT          IN NUMBER,
            caller.addInputParameter("1");//COD_INTEG        IN NUMBER,
            caller.addInputParameter(getLogin());//LOGIN            IN VARCHAR2,
            caller.addInputParameter(parametrosExecutacao.get("UP"));//UP_INI           IN NUMBER,
            caller.addInputParameter(parametrosExecutacao.get("UP"));//UP_FIM           IN NUMBER,
            caller.addOutputParameter(1, "LOG");//LOG_ERRO_SQL     OUT VARCHAR2,
            caller.addOutputParameter(2, "SUCESSO");//V_SUCESSO        OUT NUMBER





            caller.execute(jdbc.getConnection());

            log = caller.resultAsString("LOG");
            sucesso = caller.resultAsBigDecimal("SUCESSO");


            if (BigDecimal.ONE.equals(sucesso)) {
                executado = true;
                mensagem = "OK";
            } else {
                executado = false;
                mensagem = "Erro prcContrInsCargaEvtM001: "+log;
            }

        } catch (Exception e) {
            throw new Exception("Erro ao executar procedure prcContrInsCargaEvtM001: " + e);
        } finally {
            JapeSession.close(hnd);
            JdbcWrapper.closeSession(jdbc);
        }
        return executado;
    }
}
