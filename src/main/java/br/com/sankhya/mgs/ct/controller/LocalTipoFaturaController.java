package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.LocalTipoFaturaModel;

/**
 * Entidade: MGSCT_Local_Tipo_Fatura
 * Tabela: MGSTCTLOCALTIPOFAT
 * Chave: NULOCALTIPOFAT
 */
public class LocalTipoFaturaController {
    private LocalTipoFaturaModel model = new LocalTipoFaturaModel();

    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);

        if( persistenceEvent.getModifingFields().isModifing("CODTIPOFATURA") ){
            model.validaUpdate(persistenceEvent.getModifingFields());
        }
        //model.recalculaCamposCalculados();
    }

    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        //model.validaDadosInsert();
        //model.preencheCamposCalculados();
    }

    private void inicializaVariaveis(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO) persistenceEvent.getVo();
        model.setVo(vo);
    }

    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        //model.criaRegistrosDerivados();
    }

    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.validaDelete();
    }

    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        //model.criaRegistrosDerivados();
    }
}
