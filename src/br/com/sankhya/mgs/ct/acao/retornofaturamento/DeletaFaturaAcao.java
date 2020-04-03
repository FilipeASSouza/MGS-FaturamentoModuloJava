package br.com.sankhya.mgs.ct.acao.retornofaturamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.mgs.ct.model.retornofaturamento.DeletaFaturaModel;

import java.math.BigDecimal;

public class DeletaFaturaAcao extends DeletaAcaoSuper implements AcaoRotinaJava {
    /*MGSVCTFATURASNFE*/
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        super.init();
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0) {
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um registro");
        } else {
            for (Registro linha : linhas) {
                linhasProcessadas++;
                DeletaFaturaModel deleteFaturaModel = new DeletaFaturaModel();
                deleteFaturaModel.setParametro("NUFATURA", new BigDecimal(linha.getCampo("FATURA").toString()));
                deleteFaturaModel.executar();
                if (deleteFaturaModel.getSucesso()) {
                    linhasComSucesso++;
                } else {
                    linhasSemSucesso++;
                }
            }
        }
        contextoAcao.setMensagemRetorno(super.getMensagem());
    }
}
