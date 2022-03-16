package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

public class ProcessamentoZeroQuantidadeProcessamentoParalelo implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        int quantidadeThreads = ProcessamentoFilaParaleloModel.getQuantidadeThreads();
        ProcessamentoFilaParaleloModel.setQuantidadeThreads(0);
        contextoAcao.setMensagemRetorno("quantidadeThreads antes de zerar: "+quantidadeThreads);
    }
}
