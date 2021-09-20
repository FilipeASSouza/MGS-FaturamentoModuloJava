package br.com.sankhya.mgs.ct.processamento.processamentomodel;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.processamento.controleintegracao.IntegracaoDetalhaCustoModel;
import br.com.sankhya.modelcore.util.ProcedureCaller;

import java.math.BigDecimal;
import java.util.Map;

public class PrcContrInsLancFaturaPortal extends ProcessarSuper implements Processar {
    IntegracaoDetalhaCustoModel model;
    public PrcContrInsLancFaturaPortal() throws Exception {
        super();
        model = new IntegracaoDetalhaCustoModel(jdbc);
    }

    @Override
    public boolean executar() throws Exception {
        boolean executado = false;//todo refatorar pra super
        try {
            super.executar();

            Map<String, String> parametrosExecutacao = this.getParametrosExecutacao();//todo refatorar colocando na super


            ProcedureCaller caller = getProcedureCaller(jdbc, parametrosExecutacao);

            String log = "";
            BigDecimal sucesso = null;

            log = caller.resultAsString("LOG");
            if (log == "null" || log == null) {
                log = "";
            }
            sucesso = caller.resultAsBigDecimal("RET");
            if (BigDecimal.ONE.equals(sucesso)) {
                executado = true;
                mensagem = "OK";
                model.atualizaComplemento(numeroUnicoIntegracao, "S");//sucesso
            } else {
                executado = false;
                mensagem = "Erro PrcContrInsLancFaturaPortal: [" +sucesso.toString() +"]"+ log;
                model.atualizaComplemento(numeroUnicoIntegracao, "E");//erro
            }
        } catch (Exception e) {
            throw new Exception("Erro ao executar procedure PrcContrInsLancFaturaPortal: " + e);
        } finally {
            super.finalizar();
        }
        return executado;
    }

    private ProcedureCaller getProcedureCaller(JdbcWrapper jdbc, Map<String, String> parametrosExecutacao) throws Exception {
        ProcedureCaller caller = new ProcedureCaller("CONTR_INS_LANC_FATURA_PORTAL");
        caller.addInputParameter(new BigDecimal(parametrosExecutacao.get("V_NUM_FATURA")));
        caller.addOutputParameter(2, "RET");//RET        OUT NUMBER
        caller.execute(jdbc.getConnection());
        return caller;
    }
}
