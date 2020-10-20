package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.ValoresEventosModel;

/**
 * Entidade: AD_CTCONTRVLRPS
 * Tabela: AD_CTCONTRVLRPS
 * Chave: SEQCONTREVENTO
 */

public class ValoresEventosController {
    ValoresEventosModel model = new ValoresEventosModel();

    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.validaDadosInsert();
        //model.preecheCamposCalculados();
    }
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        //model.validaCamposUpdate(persistenceEvent.getModifingFields());
//        model.validaDadosUpdate();
//        DynamicVO oldVO = (DynamicVO) persistenceEvent.getOldVO();
//        model.validaDadosUpdate(oldVO);
        //model.recalculaCamposCalculados();
        model.validaDadosInsert();

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
        //model.validaDelete();
    }

    private void inicializaVariaveis(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO) persistenceEvent.getVo();
        model.setVo(vo);
    }
}
