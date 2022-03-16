package br.com.sankhya.mgs.ct.acao.retornofaturamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.mgs.ct.model.retornofaturamento.DeletaDetalhamentoModel;

import java.math.BigDecimal;

public class DeletaDetalhamentoAcao extends DeletaAcaoSuper implements AcaoRotinaJava {
    /*MGSVCTDETALHAINTEGRA*/
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0) {
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um registro");
        } else {
            for (Registro linha : linhas) {
                linhasProcessadas++;
                DeletaDetalhamentoModel deletaDetalhamentoModel = new DeletaDetalhamentoModel();
                deletaDetalhamentoModel.setParametro("NUINTEGRADC", new BigDecimal(linha.getCampo("NUINTEGRADC").toString()));
                deletaDetalhamentoModel.setParametro("POSSUIPLANILHAFISCAL", linha.getCampo("PLANILHA_FISCAL").toString());
                deletaDetalhamentoModel.executar();
                if (deletaDetalhamentoModel.getSucesso()) {
                    linhasComSucesso++;
                } else {
                    linhasSemSucesso++;
                }
            }
        }
        contextoAcao.setMensagemRetorno(super.getMensagem());
    }
}
