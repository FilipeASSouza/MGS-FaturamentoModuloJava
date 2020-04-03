package br.com.sankhya.mgs.ct.model.edicaodetalhamento;

public class EdicaoDataLancamentoModel extends EdicaoDetalhamentoModelSuper{
    public void executar() throws Exception {
        scriptsql = "updateDataLancamento.sql";
        super.inicializarExecutar();
        nativeSqlDecorator.setParametro("V_DTLCCUSTO",parametros.get("DTLCCUSTO"));//dd/mm/yyyy
        super.finalizaExecutar();
    }
}
