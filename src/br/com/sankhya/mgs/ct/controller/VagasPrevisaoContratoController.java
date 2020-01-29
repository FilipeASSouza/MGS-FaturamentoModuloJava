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
        DynamicVO oldVO = (DynamicVO) persistenceEvent.getOldVO();
        inicializaVariaveis(persistenceEvent);
        vagasPrevisaoContratoModel.validaDadosUpdate(oldVO);
    }

    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        vagasPrevisaoContratoModel.alteraDadosDerivados();

    }

    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        vagasPrevisaoContratoModel.validaDelete();
    }

    private void inicializaVariaveis(PersistenceEvent persistenceEvent){
        DynamicVO vo = (DynamicVO) persistenceEvent.getVo();
        vagasPrevisaoContratoModel.setVo(vo);
    }
}
