package br.com.sankhya.mgs.ct.acao.retornofaturamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.mgs.ct.model.retornofaturamento.DeletaPlanilhaModel;

import java.math.BigDecimal;

public class DeletaPlanilhaAcao extends DeletaAcaoSuper implements AcaoRotinaJava {
    /*MGSVCTPLANILHAS*/
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        super.init();
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0) {
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um registro");
        } else {
            for (Registro linha : linhas) {
                linhasProcessadas++;
                DeletaPlanilhaModel deletaPlanilhaModel = new DeletaPlanilhaModel();
                deletaPlanilhaModel.setParametro("NUINTEGRALC", new BigDecimal(linha.getCampo("COD_INTEGRA").toString()));
                deletaPlanilhaModel.setParametro("TIPGESTOR", linha.getCampo("TIPGESTOR").toString());
                deletaPlanilhaModel.executar();
                if (deletaPlanilhaModel.getSucesso()) {
                    linhasComSucesso++;
                } else {
                    linhasSemSucesso++;
                }
            }
        }
        contextoAcao.setMensagemRetorno(super.getMensagem());
    }
}
