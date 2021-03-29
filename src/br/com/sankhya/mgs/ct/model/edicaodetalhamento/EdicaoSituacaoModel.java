package br.com.sankhya.mgs.ct.model.edicaodetalhamento;


import java.math.BigDecimal;

public class EdicaoSituacaoModel extends EdicaoDetalhamentoModelSuper{
    public void executar() throws Exception {
        scriptsql = "updateSituacao.sql";
        super.inicializarExecutar();
        nativeSqlDecorator.setParametro("V_SIT", BigDecimal.valueOf(Long.parseLong(parametros.get("SITLANC").toString())));//0 é ativo, 1 é inativo
        super.finalizaExecutar();
    }
}
