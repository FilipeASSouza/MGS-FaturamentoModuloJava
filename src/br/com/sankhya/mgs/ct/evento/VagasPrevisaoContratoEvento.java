package br.com.sankhya.mgs.ct.evento;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.mgs.ct.controller.VagasPrevisaoContratoController;

public class VagasPrevisaoContratoEvento implements EventoProgramavelJava {
    private VagasPrevisaoContratoController vagasPrevisaoContratoController = new VagasPrevisaoContratoController();

    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {

        vagasPrevisaoContratoController.beforeUpdate(persistenceEvent);
    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        new VagasPrevisaoContratoController().beforeDelete(persistenceEvent);

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {

        vagasPrevisaoContratoController.afterUpdate(persistenceEvent);
    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }
}
