package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.VagasPrevisaoContratoModel;

public class VagasPrevisaoContratoController {
    VagasPrevisaoContratoModel vagasPrevisaoContratoModel;
    public VagasPrevisaoContratoController() {
        vagasPrevisaoContratoModel = new VagasPrevisaoContratoModel();
    }

    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO) persistenceEvent.getVo();
        DynamicVO oldVO = (DynamicVO) persistenceEvent.getOldVO();
        vagasPrevisaoContratoModel.setVo(vo);
        vagasPrevisaoContratoModel.validaDadosUpdate(oldVO);
    }

    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO) persistenceEvent.getVo();
        vagasPrevisaoContratoModel.setVo(vo);
        vagasPrevisaoContratoModel.alteraDadosDerivados();

    }

}
