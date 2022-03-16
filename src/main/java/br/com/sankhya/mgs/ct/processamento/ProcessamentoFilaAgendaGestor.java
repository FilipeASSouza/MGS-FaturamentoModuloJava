package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import java.math.BigDecimal;

public class ProcessamentoFilaAgendaGestor implements ScheduledAction {
    @Override
    public void onTime(ScheduledActionContext scheduledActionContext) {

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
