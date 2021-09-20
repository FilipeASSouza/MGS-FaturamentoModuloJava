package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.PrevisoesContratoModel;

/**
 * Entidade: MGSCT_Previsoes_Contrato
 * Tabela: MGSTCTCONTRATOPREV
 * Chave: NUCONTRPREV
 */

public class PrevisoesContratoController {
    PrevisoesContratoModel model;
    public PrevisoesContratoController() {
    
    }

    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.validaDadosInsert();
        model.preencheCamposCalculados();
    }
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.validaCamposUpdate(persistenceEvent.getModifingFields());
        model.recalculaCamposCalculados();
    }

    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.criaRegistrosDerivados();
    }

    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.criaRegistrosDerivados();
    }

    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.validaDelete();
    }

    private void inicializaVariaveis(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO) persistenceEvent.getVo();
        model = new PrevisoesContratoModel(vo,persistenceEvent.getJdbcWrapper());
    }
}
