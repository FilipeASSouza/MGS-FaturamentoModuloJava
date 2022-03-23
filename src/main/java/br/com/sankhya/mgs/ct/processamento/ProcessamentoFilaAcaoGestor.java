package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ProcessamentoFilaAcaoGestor implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        JdbcWrapper jdbcWrapper = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
        try {
            ProcessamentoFilaModel.getInstance("gestor", jdbcWrapper).executar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}