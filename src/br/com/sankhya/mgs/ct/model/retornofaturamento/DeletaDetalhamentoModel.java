package br.com.sankhya.mgs.ct.model.retornofaturamento;

import java.math.BigDecimal;

public class DeletaDetalhamentoModel extends DeletaModelSuper {
    public void executar() throws Exception {
        nomeProcedure = "CONTR_INS_LANC_DELETE_CCINTEGR";
        super.inicializarExecutar();

        caller.addInputParameter((BigDecimal)parametros.get("NUINTEGRADC"));
        caller.addInputParameter((String)parametros.get("POSSUIPLANILHAFISCAL"));
        caller.addOutputParameter(2, "RET");
        caller.execute(jdbc.getConnection());

        BigDecimal retorno = null;
        retorno = caller.resultAsBigDecimal("RET");

         sucesso = BigDecimal.ONE.equals(retorno);

        super.finalizaExecutar();
    }
}
