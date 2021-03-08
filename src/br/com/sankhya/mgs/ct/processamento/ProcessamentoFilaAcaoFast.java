package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

public class ProcessamentoFilaAcaoFast implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        ProcessamentoFilaModelFast.getInstance().executar();
    }
}
