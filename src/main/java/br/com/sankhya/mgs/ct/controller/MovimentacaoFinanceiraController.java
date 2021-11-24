package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.MovimentacaoFinanceiraModel;

/**
 * Entidade: Financeiro
 * Tabela: TGFFIN
 * Chave: NUFIN
 */

public class MovimentacaoFinanceiraController {
    MovimentacaoFinanceiraModel model = new MovimentacaoFinanceiraModel();

    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        //model.validaDadosInsert();
        //model.preecheCamposCalculados();
    }
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        //model.validaCamposUpdate(persistenceEvent.getModifingFields());
        //model.validaDadosUpdate(persistenceEvent);
        model.deletarPagamentoFatura(persistenceEvent);
//        DynamicVO oldVO = (DynamicVO) persistenceEvent.getOldVO();
//        model.validaDadosUpdate(oldVO);
        //model.recalculaCamposCalculados();

    }

    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        //model.criaRegistrosDerivados();
    }

    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        //model.criaRegistrosDerivados();
    }

    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.deletarPagamentoFatura(persistenceEvent);
        //model.validaDelete();
    }

    private void inicializaVariaveis(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO) persistenceEvent.getVo();
        model.setVo(vo);
    }
}
