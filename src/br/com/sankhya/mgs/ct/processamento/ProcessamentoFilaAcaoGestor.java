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

            if( quantidadeExecucao.compareTo(BigDecimal.valueOf(40L)) < 0 ){
                ProcessamentoFilaModelGestor.getInstance().executar();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
