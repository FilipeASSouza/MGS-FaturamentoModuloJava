package br.com.sankhya.mgs.ct.model.retornofaturamento;

import java.math.BigDecimal;

public class DeletaPlanilhaModel extends DeletaModelSuper {
    public void executar() throws Exception {
        nomeProcedure = "CONTR_INS_LANC_DELETE_PLANILHA";
        super.inicializarExecutar();

        caller.addInputParameter((BigDecimal)parametros.get("NUINTEGRALC"));
        caller.addInputParameter((String)parametros.get("TIPGESTOR"));
        caller.addOutputParameter(2, "RET");
        caller.execute(jdbc.getConnection());


        BigDecimal retorno;
        retorno = caller.resultAsBigDecimal("RET");

        sucesso = BigDecimal.ONE.equals(retorno);


        super.finalizaExecutar();
    }
}
