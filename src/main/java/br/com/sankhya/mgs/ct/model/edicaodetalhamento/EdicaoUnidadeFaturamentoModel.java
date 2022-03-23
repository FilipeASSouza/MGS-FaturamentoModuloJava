package br.com.sankhya.mgs.ct.model.edicaodetalhamento;

import br.com.sankhya.jape.dao.JdbcWrapper;

public class EdicaoUnidadeFaturamentoModel extends EdicaoDetalhamentoModelSuper {
    public EdicaoUnidadeFaturamentoModel(JdbcWrapper jdbc) throws Exception {
        super("updateUnidadeFaturamento.sql", jdbc);
    }

    public void executar() throws Exception {
        super.inicializarExecutar();
        nativeSqlDecorator.setParametro("V_UNIDADE", parametros.get("CODUNIDADEFATUR"));
        super.finalizaExecutar();
    }
}
