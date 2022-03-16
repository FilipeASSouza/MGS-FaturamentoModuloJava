package br.com.sankhya.mgs.ct.evento;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.mgs.ct.controller.PagamentoFaturaController;

public class PagamentoFaturaEvento implements EventoProgramavelJava {

    private PagamentoFaturaController controller = new PagamentoFaturaController();

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
//        controller.beforeUpdate(persistenceEvent);
    }

    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) { }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) { }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) { }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) { }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) { }

    @Override
    public void beforeCommit(TransactionContext transactionContext) { }
}
