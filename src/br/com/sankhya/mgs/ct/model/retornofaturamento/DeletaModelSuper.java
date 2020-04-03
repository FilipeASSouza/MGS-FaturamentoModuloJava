package br.com.sankhya.mgs.ct.model.retornofaturamento;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.ProcedureCaller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DeletaModelSuper {
    protected JapeSession.SessionHandle hnd = null;
    protected JdbcWrapper jdbc = null;
    protected String mensagem;
    protected Map<String, Object> parametros = new HashMap<String, Object>();
    protected ProcedureCaller caller;
    protected String nomeProcedure;
    protected Boolean sucesso;

    public void setParametro(String nomeParametro, Object valorParametro){
        parametros.put(nomeParametro, valorParametro);
    }

    protected void inicializarExecutar() throws SQLException {
        hnd = JapeSession.open();
        final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        jdbc = dwfFacade.getJdbcWrapper();
        jdbc.openSession();
        caller = new ProcedureCaller(nomeProcedure);

    }

    protected void finalizaExecutar(){

    }

    public Boolean getSucesso() {
        return sucesso;
    }
}
