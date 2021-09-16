package br.com.sankhya.mgs.ct.model.edicaodetalhamento;

import br.com.sankhya.jape.dao.JdbcWrapper;

public class EdicaoDataLancamentoModel extends EdicaoDetalhamentoModelSuper{
    public EdicaoDataLancamentoModel(JdbcWrapper jdbc) throws Exception {
        super("updateDataLancamento.sql", jdbc);
    }
    
    public void executar() throws Exception {
        super.inicializarExecutar();
        nativeSqlDecorator.setParametro("V_DTLCCUSTO",parametros.get("DTLCCUSTO"));//dd/mm/yyyy
        super.finalizaExecutar();
    }
}
