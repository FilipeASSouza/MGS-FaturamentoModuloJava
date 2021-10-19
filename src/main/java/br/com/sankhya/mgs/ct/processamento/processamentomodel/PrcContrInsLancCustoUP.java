package br.com.sankhya.mgs.ct.processamento.processamentomodel;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel.GeraFilaContrInsLancCustoUPAnexo;
import br.com.sankhya.mgs.ct.processamento.controleintegracao.IntegracaoLancamentoCustoModel;
import br.com.sankhya.modelcore.util.ProcedureCaller;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.util.Map;

public class PrcContrInsLancCustoUP extends ProcessarSuper implements Processar {
    public PrcContrInsLancCustoUP() {
        super();
    }

    @Override
    public boolean executar() throws Exception {
        Boolean executado = false;//todo refatorar pra super
        try {
            super.executar();

            Map<String, String> parametrosExecutacao = this.getParametrosExecutacao();//todo refatorar colocando na super

            boolean integracaoSalva = geraIntegracao(parametrosExecutacao);

            if (!integracaoSalva) {
                IntegracaoLancamentoCustoModel.atualizaComplemento(numeroUnicoIntegracao, "S");
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
                    mensagem = "OK";
                    IntegracaoLancamentoCustoModel.atualizaComplemento(numeroUnicoIntegracao, "S");//sucesso
                    geraFilaAnexo(parametrosExecutacao);
                } else {
                    executado = false;
                    mensagem = "Erro PrcContrInsLancCustoUP: " + log;
                    IntegracaoLancamentoCustoModel.atualizaComplemento(numeroUnicoIntegracao, "E");//erro
                }
            }
        } catch (Exception e) {
            throw new Exception("Erro ao executar procedure PrcContrInsLancCustoUP: " + e);
        } finally {
            super.finalizar();
        }
        return executado;
    }

    private ProcedureCaller getProcedureCaller(JdbcWrapper jdbc, Map<String, String> parametrosExecutacao) throws Exception {
        ProcedureCaller caller = new ProcedureCaller("CONTR_INS_LANC_CUSTO_UP");

        caller.addInputParameter(parametrosExecutacao.get("V_CONTRATO"));//V_CONTRATO        IN NUMBER,
        caller.addInputParameter(parametrosExecutacao.get("V_MODALIDADE"));//V_MODALIDADE      IN NUMBER,
        caller.addInputParameter(parametrosExecutacao.get("V_MESFATURAMENTO"));//V_MESFATURAMENTO  IN NUMBER,
        caller.addInputParameter(TimeUtils.toTimestamp(parametrosExecutacao.get("V_DTLCCUSTO"),"yyyyMMdd"));//V_DTLCCUSTO       IN DATE,
        caller.addInputParameter(parametrosExecutacao.get("V_TIPOFATU"));//V_TIPOFATU        IN NUMBER,
        caller.addInputParameter(parametrosExecutacao.get("V_UNIDADEFAT"));//V_UNIDADEFAT      IN NUMBER,
        caller.addInputParameter(numeroUnicoIntegracao);//V_CODINTLC        IN NUMBER,
        caller.addInputParameter(getLogin());//V_LOGIN           IN VARCHAR2,
        caller.addOutputParameter(1, "LOG");//LOG_ERRO_SQL     OUT VARCHAR2,
        caller.addOutputParameter(2, "SUCESSO");//V_SUCESSO        OUT NUMBER
        caller.execute(jdbc.getConnection());
        return caller;
    }

    private boolean geraIntegracao(Map<String, String> parametrosExecutacao) throws Exception {
        IntegracaoLancamentoCustoModel integracaoLancamentoCustoModel = new IntegracaoLancamentoCustoModel();
        IntegracaoLancamentoCustoModel.IntegracaoDetalhaCustoPOJO dadosIntegracao = integracaoLancamentoCustoModel.getPojo();
        dadosIntegracao.setNumeroContrato(new BigDecimal(parametrosExecutacao.get("V_CONTRATO")));
        dadosIntegracao.setNumeroUnicoModalidade(new BigDecimal(parametrosExecutacao.get("V_MODALIDADE")));
        dadosIntegracao.setCodigoCompetencia(new BigDecimal(parametrosExecutacao.get("V_MESFATURAMENTO")));
        dadosIntegracao.setCodigoPeriodo(new BigDecimal(parametrosExecutacao.get("V_MESFATURAMENTO")));
        dadosIntegracao.setCodigoUnidadeFaturamento(new BigDecimal(parametrosExecutacao.get("V_UNIDADEFAT")));
        dadosIntegracao.setCodigoTipoFatura(new BigDecimal(parametrosExecutacao.get("V_TIPOFATU")));
        dadosIntegracao.setDataCusto(TimeUtils.toTimestamp(parametrosExecutacao.get("V_DTLCCUSTO"),"yyyyMMdd"));

        Boolean integracaoSalva = integracaoLancamentoCustoModel.salvarIntegracao(dadosIntegracao);
        numeroUnicoIntegracao = integracaoLancamentoCustoModel.getNumeroUnicoIntegracao();
        return integracaoSalva;
    }

    private void geraFilaAnexo(Map<String, String> parametrosExecutacao) throws Exception {
        GeraFilaContrInsLancCustoUPAnexo geraFilaContrInsLancCustoUPAnexo = new GeraFilaContrInsLancCustoUPAnexo();//todo arrumar para usar itnerface java e classe fabrica

        geraFilaContrInsLancCustoUPAnexo.setParametroExecucao("dataCusto",TimeUtils.toTimestamp(parametrosExecutacao.get("V_DTLCCUSTO"),"yyyyMMdd"));
        geraFilaContrInsLancCustoUPAnexo.setParametroExecucao("numeroContrato",new BigDecimal(parametrosExecutacao.get("V_CONTRATO")));
        geraFilaContrInsLancCustoUPAnexo.setParametroExecucao("numeroUnidadeFaturamento",new BigDecimal(parametrosExecutacao.get("V_UNIDADEFAT")));
        geraFilaContrInsLancCustoUPAnexo.setParametroExecucao("codigoTipoFatura",new BigDecimal(parametrosExecutacao.get("V_TIPOFATU")));
        geraFilaContrInsLancCustoUPAnexo.setParametroExecucao("codigoUsuario",registroFila.getCODUSU());
        geraFilaContrInsLancCustoUPAnexo.executar();
    }
}
