package br.com.sankhya.mgs.ct.evento;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.mgs.ct.controller.UnidadesController;

public class UnidadesEvento implements EventoProgramavelJava {

    private UnidadesController controller = new UnidadesController();

    public UnidadesEvento(){}

    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        this.controller.beforeInsert(persistenceEvent);
    }

    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        this.controller.beforeUpdate(persistenceEvent);
    }

    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        this.controller.beforeDelete(persistenceEvent);
    }

    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        this.controller.afterInsert(persistenceEvent);
    }

    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        this.controller.afterUpdate(persistenceEvent);
    }

    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {
    }

    public void beforeCommit(TransactionContext transactionContext) throws Exception {
    }
}
