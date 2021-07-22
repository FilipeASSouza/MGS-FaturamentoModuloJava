package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

public class ProcessamentoFilaAcaoGestor implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        ProcessamentoFilaModelGestor.getInstance().executar();
    }
}
