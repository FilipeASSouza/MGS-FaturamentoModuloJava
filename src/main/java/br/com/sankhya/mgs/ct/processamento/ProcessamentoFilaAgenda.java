package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

public class ProcessamentoFilaAgenda implements ScheduledAction {

    @Override
    public void onTime(ScheduledActionContext scheduledActionContext) {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JdbcWrapper jdbcWrapper = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            ProcessamentoFilaModel.getInstance("normal", jdbcWrapper).executar();
        } catch (Exception e) {
            e.printStackTrace();
            scheduledActionContext.log(e.getMessage());
        }
    }
}
