package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.UnidadesModel;

public class UnidadesController {
    UnidadesModel model = new UnidadesModel();

    public UnidadesController() {
    }

    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        this.inicializaVariaveis(persistenceEvent);
        this.model.preecheCamposCalculados();
        this.model.criaRegistrosDerivados();
    }

    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        this.inicializaVariaveis(persistenceEvent);
//        DynamicVO oldVO = (DynamicVO)persistenceEvent.getOldVO();
        this.model.criaRegistrosDerivados();
    }

    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        this.inicializaVariaveis(persistenceEvent);

    }

    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        this.inicializaVariaveis(persistenceEvent);

    }

    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        this.inicializaVariaveis(persistenceEvent);
    }

    private void inicializaVariaveis(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO)persistenceEvent.getVo();
        this.model.setVo(vo);
    }
}
