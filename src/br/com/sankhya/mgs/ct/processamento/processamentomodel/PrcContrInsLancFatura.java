package br.com.sankhya.mgs.ct.processamento.processamentomodel;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.processamento.controleintegracao.IntegracaoDetalhaCustoModel;
import br.com.sankhya.modelcore.util.ProcedureCaller;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.util.Map;

public class PrcContrInsLancFatura extends ProcessarSuper implements Processar {
    public PrcContrInsLancFatura() {
        super();
    }

    @Override
    public boolean executar() throws Exception {
        Boolean executado = false;//todo refatorar pra super
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
            sucesso = caller.resultAsBigDecimal("SUCESSO");
            if (BigDecimal.ONE.equals(sucesso)) {
                executado = true;
                mensagem = "OK";
                IntegracaoDetalhaCustoModel.atualizaComplemento(numeroUnicoIntegracao, "S");//sucesso
            } else {
                executado = false;
                mensagem = "Erro PrcContrInsLancCustoUP: [" +sucesso.toString() +"]"+ log;
                IntegracaoDetalhaCustoModel.atualizaComplemento(numeroUnicoIntegracao, "E");//erro
            }

        } catch (Exception e) {
            throw new Exception("Erro ao executar procedure PrcContrInsLancFatura: " + e);
        } finally {

        }
        return executado;
    }

    private ProcedureCaller getProcedureCaller(JdbcWrapper jdbc, Map<String, String> parametrosExecutacao) throws Exception {
        ProcedureCaller caller = new ProcedureCaller("CONTR_INS_LANC_FATURA");
        caller.addInputParameter(TimeUtils.toTimestamp(parametrosExecutacao.get("V_PERIODOFAT"),"yyyyMMdd"));
        caller.addInputParameter(TimeUtils.toTimestamp(parametrosExecutacao.get("V_DATAEMISSAO"),"yyyyMMdd"));
        caller.addInputParameter(TimeUtils.toTimestamp(parametrosExecutacao.get("V_DATAVENCIMENTO"),"yyyyMMdd"));
        caller.addInputParameter(parametrosExecutacao.get("V_APROVADAS"));
        caller.addInputParameter(TimeUtils.toTimestamp(parametrosExecutacao.get("V_DTLCCUSTO"),"yyyyMMdd"));
        caller.addInputParameter(parametrosExecutacao.get("V_CODTIPOFATURA"));
        caller.addInputParameter(parametrosExecutacao.get("V_CODUNIDADEFATURA"));
        caller.addInputParameter(getLogin());//V_LOGIN           IN VARCHAR2,
        caller.addOutputParameter(2, "SUCESSO");//V_SUCESSO        OUT NUMBER
        caller.execute(jdbc.getConnection());
        return caller;
    }
}
