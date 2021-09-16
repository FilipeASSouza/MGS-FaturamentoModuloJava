package br.com.sankhya.mgs.ct.model.edicaodetalhamento;

public class EdicaoSituacaoModel extends EdicaoDetalhamentoModelSuper{
    public void executar() throws Exception {
        scriptsql = "updateSituacao.sql";
        super.inicializarExecutar();
        nativeSqlDecorator.setParametro("V_SIT",parametros.get("SITLANC"));//0 é ativo, 1 é inativo
        super.finalizaExecutar();
    }
}
