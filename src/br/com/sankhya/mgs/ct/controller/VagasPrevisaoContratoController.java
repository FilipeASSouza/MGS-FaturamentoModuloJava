package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.VagasPrevisaoContratoModel;

/**
 * Entidade: MGSCT_Vagas_Previsao_Contrato
 * Tabela: MGSTCTCONTRATOVAGA
 * Chave: NUCONTRVAGA
 */

public class VagasPrevisaoContratoController {
    VagasPrevisaoContratoModel model = new VagasPrevisaoContratoModel();

    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
//        DynamicVO oldVO = (DynamicVO) persistenceEvent.getOldVO();
        inicializaVariaveis(persistenceEvent);
//        model.validaDadosUpdate(oldVO);
        //model.diminuirUmQuantidadeContrata();
        model.validaUpdate();
        model.validaCamposUpdate(persistenceEvent.getModifingFields());
    }

    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception{
        inicializaVariaveis(persistenceEvent);
        model.validaDadosInsert();
    }

    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.alteraDadosDerivados();

    }

    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.validaDelete();
    }

    private void inicializaVariaveis(PersistenceEvent persistenceEvent){
        DynamicVO vo = (DynamicVO) persistenceEvent.getVo();
        model.setVo(vo);
    }
}
