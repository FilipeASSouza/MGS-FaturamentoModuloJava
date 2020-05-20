package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.GerarFilaLancamentoFaturaPortalModel;

import java.math.BigDecimal;

public class GerarFilaLancamentoFaturaPortalAcao  implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0) {
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um registro");
        } else {
            for (Registro linha : linhas) {
                GerarFilaLancamentoFaturaPortalModel gerarFilaLancamentoFaturaPortalModel = new GerarFilaLancamentoFaturaPortalModel();
                gerarFilaLancamentoFaturaPortalModel.setNumeroFatura(new BigDecimal(linha.getCampo("FATURA").toString()));
                gerarFilaLancamentoFaturaPortalModel.gerarFila();
            }
        }
    }
}
