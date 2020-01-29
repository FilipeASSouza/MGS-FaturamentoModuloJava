package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.PrevisoesUnidadeModel;

/**
 * Entidade: MGSCT_Previsoes_Unidade
 * Tabela: MGSTCTUNIDADEPREV
 * Chave: NUUNIDPREV
 */

public class PrevisoesUnidadeController {
    private PrevisoesUnidadeModel model = new PrevisoesUnidadeModel();

    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.validaDadosInsert();
    }

    private void inicializaVariaveis(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO) persistenceEvent.getVo();
        model.setVo(vo);
    }

    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
    }
}
