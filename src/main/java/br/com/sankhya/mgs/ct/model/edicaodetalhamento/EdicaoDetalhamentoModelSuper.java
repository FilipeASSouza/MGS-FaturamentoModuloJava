package br.com.sankhya.mgs.ct.model.edicaodetalhamento;

import br.com.sankhya.bh.utils.NativeSqlDecorator;

import java.util.HashMap;
import java.util.Map;

public class EdicaoDetalhamentoModelSuper {
    protected String scriptsql;
    protected Map<String, Object> parametros = new HashMap<String, Object>();
    protected NativeSqlDecorator nativeSqlDecorator;


    public void inicializarExecutar() throws Exception{
        nativeSqlDecorator = new NativeSqlDecorator(this, scriptsql);
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
