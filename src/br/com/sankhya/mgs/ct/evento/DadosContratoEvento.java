package br.com.sankhya.mgs.ct.evento;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.mgs.ct.controller.DadosContratoController;

public class DadosContratoEvento implements EventoProgramavelJava {
    private DadosContratoController controller = new DadosContratoController();

    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        controller.beforeInsert(persistenceEvent);
    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        controller.beforeUpdate(persistenceEvent);
    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }
}
