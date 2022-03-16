package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.mgs.ct.model.NumeroNFEModel;

import java.math.BigDecimal;

public class GeraFaturaAnexoAcao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        BigDecimal numeroFaturaInicial = new BigDecimal(contextoAcao.getParam("NUFATURAI").toString());
        BigDecimal numeroFaturaFinal = new BigDecimal(contextoAcao.getParam("NUFATURAF").toString());
        new NumeroNFEModel().geraAnexoFatura(numeroFaturaInicial, numeroFaturaFinal);
    }
}
