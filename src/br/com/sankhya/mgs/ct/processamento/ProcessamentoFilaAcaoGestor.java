package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

import java.math.BigDecimal;

public class ProcessamentoFilaAcaoGestor implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        BigDecimal quantidadeExecucao = null;

        try{

            NativeSqlDecorator verificarQuantidadeProcessosSQL = new NativeSqlDecorator("SELECT " +
                    " COUNT(*) QTD " +
                    " FROM MGSTCTFILAPROC " +
                    " WHERE STATUS = 'A' ");
            if( verificarQuantidadeProcessosSQL.proximo() ){
                quantidadeExecucao = verificarQuantidadeProcessosSQL.getValorBigDecimal("QTD");
            }

            if( BigDecimal.valueOf(40L).compareTo(quantidadeExecucao) > 0 ){
                ProcessamentoFilaModel.getInstance("gestor").executar();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
