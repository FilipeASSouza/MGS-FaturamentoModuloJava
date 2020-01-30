package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.VagasPrevisaoUnidadeModel;

/**
 * Entidade: MGSCT_Vagas_Previsao_Unidade
 * Tabela: MGSTCTUNIDPREVVAGA
 * Chave: NUUNIDPREVVAGA
 */

public class VagasPrevisaoUnidadeController {
    VagasPrevisaoUnidadeModel model = new VagasPrevisaoUnidadeModel();

    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO oldVO = (DynamicVO) persistenceEvent.getOldVO();
        inicializaVariaveis(persistenceEvent);
        model.validaDadosUpdate(oldVO);
    }

    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.alteraDadosDerivados();

    }

    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.validaDelete();
    }

    private void inicializaVariaveis(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO) persistenceEvent.getVo();
        model.setVo(vo);
    }
}
