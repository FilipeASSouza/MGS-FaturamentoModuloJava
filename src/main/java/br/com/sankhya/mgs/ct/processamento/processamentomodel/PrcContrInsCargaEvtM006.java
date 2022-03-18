/***
 * Created by: Natyeli Abreu
 * Date: 04/03/2020
 */
package br.com.sankhya.mgs.ct.processamento.processamentomodel;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.processamento.controleintegracao.IntegracaoDetalhaCustoModel;
import br.com.sankhya.modelcore.util.ProcedureCaller;

import java.math.BigDecimal;
import java.util.Map;

public class PrcContrInsCargaEvtM006 extends ProcessarSuper implements Processar {

    public PrcContrInsCargaEvtM006() {
        super();
    }

    @Override
    public boolean executar() throws Exception {
        Boolean executado = false;
        try {
            super.executar();

            Map<String, String> parametrosExecutacao = this.getParametrosExecutacao();

            boolean integracaoSalva = geraIntegracao(parametrosExecutacao);

            if (!integracaoSalva) {
                IntegracaoDetalhaCustoModel.atualizaComplemento(numeroUnicoIntegracao, "S");
                executado = true;
                mensagem = "Processado Integraçao: " + numeroUnicoIntegracao;
            } else {

                String log = "";
                BigDecimal sucesso = null;


                ProcedureCaller caller = getProcedureCaller(jdbc, parametrosExecutacao);

                log = caller.resultAsString("LOG");
                if (log == "null"){
                    log = "";
                }
                sucesso = caller.resultAsBigDecimal("SUCESSO");
                if (BigDecimal.ONE.equals(sucesso)) {
                    executado = true;
                    mensagem = log;
                    IntegracaoDetalhaCustoModel.atualizaComplemento(numeroUnicoIntegracao, "S");//sucesso
                } else {
                    executado = false;
                    mensagem = "Erro prcContrInsCargaEvtM006: " + log;
                    IntegracaoDetalhaCustoModel.atualizaComplemento(numeroUnicoIntegracao, "E");//erro
                }
            }
        } catch (Exception e) {
            throw new Exception("Erro ao executar procedure prcContrInsCargaEvtM006: " + e);
        } finally {
            super.finalizar();
        }
        return executado;
    }

    private ProcedureCaller getProcedureCaller(JdbcWrapper jdbc, Map<String, String> parametrosExecutacao) throws Exception {
        ProcedureCaller caller = new ProcedureCaller("CONTR_INS_CARGA_EVT_M_006");
        caller.addInputParameter(parametrosExecutacao.get("V_CONTRATO"));//V_CONTRATO       IN NUMBER,
        caller.addInputParameter(null);//VTP_VAGA         IN VARCHAR2,
        caller.addInputParameter(parametrosExecutacao.get("V_TP_APONTAMENTO"));//V_TP_APONTAMENTO IN VARCHAR2,
        caller.addInputParameter(parametrosExecutacao.get("MES_CARGA"));//MES_CARGA        IN NUMBER,
        caller.addInputParameter(parametrosExecutacao.get("MES_FAT"));//MES_FAT          IN NUMBER,
        caller.addInputParameter(numeroUnicoIntegracao);//COD_INTEG        IN NUMBER,
        caller.addInputParameter(getLogin());//LOGIN            IN VARCHAR2,
        caller.addInputParameter(parametrosExecutacao.get("UP"));//UP_INI           IN NUMBER,
        caller.addInputParameter(parametrosExecutacao.get("P_PARAM"));//UP_FIM           IN NUMBER,
        caller.addOutputParameter(1, "LOG");//LOG_ERRO_SQL     OUT VARCHAR2,
        caller.addOutputParameter(2, "SUCESSO");//V_SUCESSO        OUT NUMBER
        caller.execute(jdbc.getConnection());
        return caller;
    }

    private boolean geraIntegracao(Map<String, String> parametrosExecutacao) throws Exception {
        IntegracaoDetalhaCustoModel integracaoDetalhaCustoModel = new IntegracaoDetalhaCustoModel();
        IntegracaoDetalhaCustoModel.IntegracaoDetalhaCustoPOJO dadosIntegracao = integracaoDetalhaCustoModel.getPojo();
        dadosIntegracao.setNumeroContrato(new BigDecimal(parametrosExecutacao.get("V_CONTRATO")));
        dadosIntegracao.setCodigpUnidadeFaturamento(new BigDecimal(parametrosExecutacao.get("UP")));
        dadosIntegracao.setCodigoOrigem(registroFila.getNUTIPOPROC());
        dadosIntegracao.setCodigoUsuarioInsercao(registroFila.getCODUSU());
        dadosIntegracao.setCodigoCompetencia(new BigDecimal(parametrosExecutacao.get("MES_CARGA")));
        dadosIntegracao.setCodigoPeriodo(new BigDecimal(parametrosExecutacao.get("MES_FAT")));
        Boolean integracaoSalva = integracaoDetalhaCustoModel.salvarIntegracao(dadosIntegracao);
        numeroUnicoIntegracao = integracaoDetalhaCustoModel.getNumeroUnicoIntegracao();
        return integracaoSalva;
    }
}
