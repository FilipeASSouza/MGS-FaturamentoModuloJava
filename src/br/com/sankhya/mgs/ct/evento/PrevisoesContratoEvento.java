package br.com.sankhya.mgs.ct.evento;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.mgs.ct.controller.PrevisoesContratoController;

/**
 * Entidade: MGSCT_Previsoes_Contrato
 * Tabela: MGSTCTCONTRATOPREV
 * Chave: NUCONTRPREV
 */

public class PrevisoesContratoEvento implements EventoProgramavelJava {
    private PrevisoesContratoController previsoesContratoController = new PrevisoesContratoController();


    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        previsoesContratoController.beforeInsert(persistenceEvent);
    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        previsoesContratoController.beforeUpdate(persistenceEvent);
    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        previsoesContratoController.beforeDelete(persistenceEvent);
    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        previsoesContratoController.afterInsert(persistenceEvent);
    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        previsoesContratoController.afterUpdate(persistenceEvent);
    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }
}
