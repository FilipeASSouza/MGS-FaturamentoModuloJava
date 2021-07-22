package br.com.sankhya.mgs.ct.processamento;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

public class ProcessamentoFilaAgendaFiscal implements ScheduledAction {
    @Override
    public void onTime(ScheduledActionContext scheduledActionContext) {
        ProcessamentoFilaModelFiscal.getInstance().executar();
    }
}
