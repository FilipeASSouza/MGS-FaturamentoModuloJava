package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.GeraFilaLancamentoCustoModel;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GeraFilaLancamentoCustoAcao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0){
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um contrato");
        } else {
            Timestamp dataReferencia = Timestamp.valueOf(contextoAcao.getParam("DTREF").toString());
            Timestamp dataCusto = Timestamp.valueOf(contextoAcao.getParam("DTCUSTO").toString());

            BigDecimal codigoTipoFatura = new BigDecimal(contextoAcao.getParam("CODTIPOFATURA").toString());//todo corrigir tipo fatura


            BigDecimal codigoUnidadeFaturamentoInicial = null;
            if (contextoAcao.getParam("CODSITEI") != null) {
                codigoUnidadeFaturamentoInicial = new BigDecimal(contextoAcao.getParam("COD").toString());
            } else {
                codigoUnidadeFaturamentoInicial = BigDecimal.ZERO;
            }
            BigDecimal codigoUnidadeFaturamentoFinal = null;
            if (contextoAcao.getParam("CODSITEF") != null) {
                codigoUnidadeFaturamentoFinal = new BigDecimal(contextoAcao.getParam("CODSITEF").toString());
            } else {
                codigoUnidadeFaturamentoFinal = BigDecimal.ZERO;
            }

            for (Registro linha : linhas) {

                /*mapParametrosChave.put("V_CONTRATO", getParametroBigDecimal("numeroContrato").toString());//V_CONTRATO IN NUMBER
                mapParametrosChave.put("V_MODALIDADE", getParametroBigDecimal("codigoModalidade").toString());//V_MODALIDADE IN NUMBER
                mapParametrosChave.put("V_MESFATURAMENTO", TimeUtils.getYearMonth(getParametroTimestamp("dataReferencia")).toString());//V_MESFATURAMENTO IN NUMBER
                mapParametrosChave.put("V_DTLCCUSTO", getParametroTimestamp("dataCusto").toString());//V_DTLCCUSTO IN DATE
                mapParametrosChave.put("V_TIPOFATU", getParametroBigDecimal("tipoFatura").toString());//V_TIPOFATU IN NUMBER
                mapParametrosChave.put("V_UNIDADEFAT", getParametroBigDecimal("numeroUnidadeFaturamento").toString());//V_UNIDADEFAT IN NUMBER*/


                GeraFilaLancamentoCustoModel geraFilaLancamentoCustoModel = new GeraFilaLancamentoCustoModel();
                geraFilaLancamentoCustoModel.setNumeroContrato((BigDecimal)linha.getCampo("NUMCONTRATO"));
                geraFilaLancamentoCustoModel.setNumeroModalidade((BigDecimal)linha.getCampo("CODTPN"));
                geraFilaLancamentoCustoModel.setDataReferencia(dataReferencia);
                geraFilaLancamentoCustoModel.setDataCusto(dataCusto);
                geraFilaLancamentoCustoModel.setCodigoTipoFatura(codigoTipoFatura);

                geraFilaLancamentoCustoModel.setUnidadeFaturamentoInicial(codigoUnidadeFaturamentoInicial);
                geraFilaLancamentoCustoModel.setUnidadeFaturamentoFinal(codigoUnidadeFaturamentoFinal);

                geraFilaLancamentoCustoModel.gerarFila();
            }
        }
    }
}
