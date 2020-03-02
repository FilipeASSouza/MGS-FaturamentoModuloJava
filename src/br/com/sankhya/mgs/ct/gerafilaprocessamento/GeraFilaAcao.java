package br.com.sankhya.mgs.ct.gerafilaprocessamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GeraFilaAcao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0){
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um contrato");
        } else {
            Timestamp dataReferencia = Timestamp.valueOf(contextoAcao.getParam("DTREF").toString());
            BigDecimal codigoUnidadeFaturamentoInicial = null;
            if (contextoAcao.getParam("CODSITEI") != null) {
                codigoUnidadeFaturamentoInicial = new BigDecimal(contextoAcao.getParam("CODSITEI").toString());
            } else {
                codigoUnidadeFaturamentoInicial = BigDecimal.ZERO;
            }
            BigDecimal codigounidadeFaturamentoFinal = null;
            if (contextoAcao.getParam("CODSITEF") != null) {
                codigounidadeFaturamentoFinal = new BigDecimal(contextoAcao.getParam("CODSITEF").toString());
            } else {
                codigounidadeFaturamentoFinal = BigDecimal.ZERO;
            }

            for (Registro linha : linhas) {


                GeraFilaModel geraFilaModel = new GeraFilaModel();
                geraFilaModel.setDataReferencia(dataReferencia);
                geraFilaModel.setNumeroContrato((BigDecimal)linha.getCampo("NUMCONTRATO"));
                geraFilaModel.setUnidadeFaturamentoInicial(codigoUnidadeFaturamentoInicial);
                geraFilaModel.setUnidadeFaturamentoFinal(codigounidadeFaturamentoFinal);
                geraFilaModel.gerarFila();
            }
        }
    }
}
