package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.PrevisoesContratoModel;

public class PrevisoesContratoController {
    PrevisoesContratoModel previsoesContratoModel;
    public PrevisoesContratoController() {
        previsoesContratoModel = new PrevisoesContratoModel();
    }

    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO)persistenceEvent.getVo();
        previsoesContratoModel.setVo(vo);
        previsoesContratoModel.validaDadosInsert();
        previsoesContratoModel.preecheCamposCalculados();
    }
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO)persistenceEvent.getVo();
        previsoesContratoModel.setVo(vo);
        //previsoesContratoModel.validaDados();
        //previsoesContratoModel.preecheCamposCalculados();
    }

    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO)persistenceEvent.getVo();
        previsoesContratoModel.setVo(vo);
        previsoesContratoModel.criaRegistrosDerivados();
    }

    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO)persistenceEvent.getVo();
        previsoesContratoModel.setVo(vo);
        previsoesContratoModel.criaRegistrosDerivados();
    }

    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO)persistenceEvent.getVo();
        previsoesContratoModel.setVo(vo);
        previsoesContratoModel.validaDelete();
    }
}
