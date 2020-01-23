package br.com.sankhya.mgs.ct.controller;


import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.mgs.ct.model.VagasPrevisaoContratoModel;

public class VagasPrevisaoContratoController {
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        VagasPrevisaoContratoModel vagasPrevisaoContratoModel = new VagasPrevisaoContratoModel();
        vagasPrevisaoContratoModel.validaDelete();

    }
}
