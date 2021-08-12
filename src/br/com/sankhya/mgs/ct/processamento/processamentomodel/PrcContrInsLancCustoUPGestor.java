package br.com.sankhya.mgs.ct.processamento.processamentomodel;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel.GeraFilaContrInsLancCustoUPGestorAnexo;
import br.com.sankhya.mgs.ct.processamento.controleintegracao.IntegracaoLancamentoCustoGestorModel;
import br.com.sankhya.modelcore.util.ProcedureCaller;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.util.Map;

public class PrcContrInsLancCustoUPGestor extends ProcessarSuper implements Processar {
    public PrcContrInsLancCustoUPGestor() {
        super();
    }

    @Override
    public boolean executar() throws Exception {
        Boolean executado = false;//todo refatorar pra super
        try {
            super.executar();

            System.out.println("Executando processamento = ");

            Map<String, String> parametrosExecutacao = this.getParametrosExecutacao();//todo refatorar colocando na super

            boolean integracaoSalva = geraIntegracao(parametrosExecutacao);

            if (!integracaoSalva) {
                IntegracaoLancamentoCustoGestorModel.atualizaComplemento(numeroUnicoIntegracao, "S");
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

                    System.out.println("Falta executar o papel ln 47 geraFilaAnexo");
                    executado = true;
                    mensagem = "OK";
                    IntegracaoLancamentoCustoGestorModel.atualizaComplemento(numeroUnicoIntegracao, "S");//sucesso
                    geraFilaAnexo(parametrosExecutacao);
                } else {
                    executado = false;
                    mensagem = "Erro PrcContrInsLancCustoUP: " + log;
                    IntegracaoLancamentoCustoGestorModel.atualizaComplemento(numeroUnicoIntegracao, "E");//erro
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
        ProcedureCaller caller = new ProcedureCaller("CONTR_INS_LANC_CUSTO_UP_GESTOR");

        caller.addInputParameter(parametrosExecutacao.get("V_CONTRATO"));//V_CONTRATO        IN NUMBER,
        caller.addInputParameter(parametrosExecutacao.get("V_DTLCCUSTO"));//V_DTLCCUSTO       IN DATE,
        caller.addInputParameter(parametrosExecutacao.get("V_UNIDADEFAT"));//V_UNIDADEFAT      IN NUMBER,
        caller.addInputParameter(parametrosExecutacao.get("V_TIPOFATU"));//V_TIPOFATU        IN NUMBER,
        caller.addInputParameter(numeroUnicoIntegracao);//V_CODINTLC        IN NUMBER,
        caller.addInputParameter(getLogin());//V_LOGIN           IN VARCHAR2,
        caller.addOutputParameter(1, "LOG");//LOG_ERRO_SQL     OUT VARCHAR2,
        caller.addOutputParameter(2, "SUCESSO");//V_SUCESSO        OUT NUMBER
        caller.execute(jdbc.getConnection());
        return caller;
    }

    private boolean geraIntegracao(Map<String, String> parametrosExecutacao) throws Exception {
        IntegracaoLancamentoCustoGestorModel integracaoLancamentoCustoGestorModel = new IntegracaoLancamentoCustoGestorModel();
        IntegracaoLancamentoCustoGestorModel.IntegracaoDetalhaCustoPOJO dadosIntegracao = integracaoLancamentoCustoGestorModel.getPojo();
        dadosIntegracao.setNumeroContrato(new BigDecimal(parametrosExecutacao.get("V_CONTRATO")));
        dadosIntegracao.setCodigoCompetencia(new BigDecimal(parametrosExecutacao.get("V_DTLCCUSTO").substring(0,6)));
        dadosIntegracao.setCodigoPeriodo(new BigDecimal(parametrosExecutacao.get("V_DTLCCUSTO").substring(0,6)));
        dadosIntegracao.setCodigoUnidadeFaturamento(new BigDecimal(parametrosExecutacao.get("V_UNIDADEFAT")));
        dadosIntegracao.setCodigoTipoFatura(new BigDecimal(parametrosExecutacao.get("V_TIPOFATU")));
        dadosIntegracao.setDataCusto(TimeUtils.toTimestamp(parametrosExecutacao.get("V_DTLCCUSTO"),"yyyyMMdd"));

        Boolean integracaoSalva = integracaoLancamentoCustoGestorModel.salvarIntegracao(dadosIntegracao);
        numeroUnicoIntegracao = integracaoLancamentoCustoGestorModel.getNumeroUnicoIntegracao();
        return integracaoSalva;
    }

    private void geraFilaAnexo(Map<String, String> parametrosExecutacao) throws Exception {
        GeraFilaContrInsLancCustoUPGestorAnexo geraFilaContrInsLancCustoUPGestorAnexo = new GeraFilaContrInsLancCustoUPGestorAnexo();//todo arrumar para usar itnerface java e classe fabrica

        geraFilaContrInsLancCustoUPGestorAnexo.setParametroExecucao("dataCusto",TimeUtils.toTimestamp(parametrosExecutacao.get("V_DTLCCUSTO"),"yyyyMMdd"));
        geraFilaContrInsLancCustoUPGestorAnexo.setParametroExecucao("numeroContrato",new BigDecimal(parametrosExecutacao.get("V_CONTRATO")));
        geraFilaContrInsLancCustoUPGestorAnexo.setParametroExecucao("numeroUnidadeFaturamento",new BigDecimal(parametrosExecutacao.get("V_UNIDADEFAT")));
        geraFilaContrInsLancCustoUPGestorAnexo.setParametroExecucao("codigoTipoFatura",new BigDecimal(parametrosExecutacao.get("V_TIPOFATU")));
        geraFilaContrInsLancCustoUPGestorAnexo.setParametroExecucao("codigoUsuario",registroFila.getCODUSU());
        geraFilaContrInsLancCustoUPGestorAnexo.executar();
    }
}
