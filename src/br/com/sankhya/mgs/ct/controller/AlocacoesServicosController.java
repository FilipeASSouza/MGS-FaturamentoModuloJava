package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgs.ct.model.AlocacoesServicosModel;

/**
 * Entidade: MGSCT_Alocacoes_Servicos
 * Tabela: MGSTCTALOCACAOSERV
 * Chave: NUALOCASERV
 */
public class AlocacoesServicosController {
    AlocacoesServicosModel model = new AlocacoesServicosModel();

    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        //model.validaDadosInsert();
        model.preencheCamposCalculados();
    }
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        model.validaCamposUpdate(persistenceEvent.getModifingFields());
        //model.validaDadosUpdate(oldVO);
        //model.recalculaCamposCalculados();

    }

    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        //model.criaRegistrosDerivados();
    }

    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        //model.criaRegistrosDerivados();
    }

    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
        inicializaVariaveis(persistenceEvent);
        //model.validaDelete();
    }

    private void inicializaVariaveis(PersistenceEvent persistenceEvent) throws Exception {
        DynamicVO vo = (DynamicVO) persistenceEvent.getVo();
        model.setVo(vo);
    }
}
