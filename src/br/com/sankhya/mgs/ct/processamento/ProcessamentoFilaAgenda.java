package br.com.sankhya.mgs.ct.processamento;

import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

public class ProcessamentoFilaAgenda implements ScheduledAction {

    @Override
    public void onTime(ScheduledActionContext scheduledActionContext) {
        ProcessamentoFilaModel.getInstance().executar();
    }
}
