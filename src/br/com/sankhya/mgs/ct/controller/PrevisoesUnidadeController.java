package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.PrevisoesUnidadeModel;

/**
 * Entidade: MGSCT_Previsoes_Contrato
 * Tabela: MGSTCTCONTRATOPREV
 */

public class PrevisoesUnidadeController {
    private PrevisoesUnidadeModel model = new PrevisoesUnidadeModel();

    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO)persistenceEvent.getVo();
        model.setVo(vo);
        model.validaDadosInsert();
    }
}
