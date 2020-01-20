package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.PrevisoesContratoModel;

public class PrevisoesContratoController {
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO)persistenceEvent.getVo();
        PrevisoesContratoModel previsoesContratoModel = new PrevisoesContratoModel(vo);
        previsoesContratoModel.validaDados();
        previsoesContratoModel.preecheCamposCalculados();
    }
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO)persistenceEvent.getVo();
        PrevisoesContratoModel previsoesContratoModel = new PrevisoesContratoModel(vo);
        previsoesContratoModel.validaDados();
        previsoesContratoModel.preecheCamposCalculados();
    }

    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO)persistenceEvent.getVo();
        PrevisoesContratoModel previsoesContratoModel = new PrevisoesContratoModel(vo);
        previsoesContratoModel.criaRegistrosDerivados();
    }

    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO)persistenceEvent.getVo();
        PrevisoesContratoModel previsoesContratoModel = new PrevisoesContratoModel(vo);
        previsoesContratoModel.criaRegistrosDerivados();
    }
}
