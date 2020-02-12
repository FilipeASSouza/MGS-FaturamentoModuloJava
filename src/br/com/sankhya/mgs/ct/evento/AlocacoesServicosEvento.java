package br.com.sankhya.mgs.ct.evento;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.mgs.ct.controller.AlocacoesServicosController;

/**
 * Entidade: MGSCT_Alocacoes_Servicos
 * Tabela: MGSTCTALOCACAOSERV
 * Chave: NUALOCASERV
 */
public class AlocacoesServicosEvento implements EventoProgramavelJava {
    private AlocacoesServicosController controller = new AlocacoesServicosController();

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
        controller.beforeDelete(persistenceEvent);
    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        controller.afterInsert(persistenceEvent);
    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        controller.afterUpdate(persistenceEvent);
    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }
}
