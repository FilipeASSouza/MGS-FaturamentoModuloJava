package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.GerarFilaLancamentoFaturaPortalModel;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;

public class GerarFilaLancamentoFaturaPortalAcao  implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0) {
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um registro");
        } else {
            JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            GerarFilaLancamentoFaturaPortalModel gerarFilaLancamentoFaturaPortalModel = new GerarFilaLancamentoFaturaPortalModel(jdbc);
            for (Registro linha : linhas) {
                gerarFilaLancamentoFaturaPortalModel.setNumeroFatura(new BigDecimal(linha.getCampo("FATURA").toString()));
                gerarFilaLancamentoFaturaPortalModel.gerarFila();
            }
        }
    }
}
