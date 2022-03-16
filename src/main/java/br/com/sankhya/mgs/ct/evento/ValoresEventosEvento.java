package br.com.sankhya.mgs.ct.evento;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.mgs.ct.controller.ValoresEventosController;

public class ValoresEventosEvento implements EventoProgramavelJava {
    private ValoresEventosController controller = new ValoresEventosController();

    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        controller.beforeInsert(persistenceEvent);
    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
//        controller.beforeInsert(persistenceEvent);
    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent)  {
    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent)  {
    }

    @Override
    public void beforeCommit(TransactionContext transactionContext)  {
    }
}
