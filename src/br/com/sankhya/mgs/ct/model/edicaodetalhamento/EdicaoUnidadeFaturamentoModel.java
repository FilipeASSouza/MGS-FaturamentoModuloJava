package br.com.sankhya.mgs.ct.model.edicaodetalhamento;

public class EdicaoUnidadeFaturamentoModel extends EdicaoDetalhamentoModelSuper{
    public void executar() throws Exception {
        scriptsql = "updateUnidadeFaturamento.sql";
        super.inicializarExecutar();
        nativeSqlDecorator.setParametro("V_UNIDADE",parametros.get("CODUNIDADEFATUR"));
        super.finalizaExecutar();
    }
}
