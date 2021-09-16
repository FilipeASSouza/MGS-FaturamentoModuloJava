package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.jape.core.JapeSession;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

public class ProcessamentoFilaAgendaFatura implements ScheduledAction {
    @Override
    public void onTime(ScheduledActionContext scheduledActionContext) {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            ProcessamentoFilaModel.getInstance("fatura").executar();
        }catch (Exception e){
            e.printStackTrace();
            scheduledActionContext.log(e.getMessage());
        }
    }
}
