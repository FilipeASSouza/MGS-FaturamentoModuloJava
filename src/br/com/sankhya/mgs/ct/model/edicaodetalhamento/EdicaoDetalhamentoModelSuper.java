package br.com.sankhya.mgs.ct.model.edicaodetalhamento;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.dao.JdbcWrapper;

import java.util.HashMap;
import java.util.Map;

public class EdicaoDetalhamentoModelSuper {
    
    protected Map<String, Object> parametros = new HashMap<String, Object>();
    protected NativeSqlDecorator nativeSqlDecorator;
    private JdbcWrapper jdbcWrapper;
    public EdicaoDetalhamentoModelSuper(String scriptsql,JdbcWrapper jdbc) throws Exception {
        this.jdbcWrapper = jdbc;
        nativeSqlDecorator = new NativeSqlDecorator(this, scriptsql,this.jdbcWrapper);
       
    }
    
    
    public void inicializarExecutar() throws Exception{
        nativeSqlDecorator.cleanParameters();
        nativeSqlDecorator.setParametro("V_NUEVTMENSAL",parametros.get("NUEVTMENSAL"));
        nativeSqlDecorator.setParametro("LOGIN",parametros.get("LOGIN"));
    }

    protected void finalizaExecutar() throws Exception {
          nativeSqlDecorator.atualizar();
    }



    public void setParametro(String nomeParametro, Object valorParametro){
        parametros.put(nomeParametro, valorParametro);
    }


}
