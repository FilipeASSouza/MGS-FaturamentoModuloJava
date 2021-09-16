package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.core.JapeSession;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import java.math.BigDecimal;

public class ProcessamentoFilaAgendaGestor implements ScheduledAction {
    @Override
    public void onTime(ScheduledActionContext scheduledActionContext) {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            
            BigDecimal quantidadeExecucao = null;
            
            try {
                
                NativeSqlDecorator verificarQuantidadeProcessosSQL = new NativeSqlDecorator("SELECT " +
                        " COUNT(*) QTD " +
                        " FROM MGSTCTFILAPROC " +
                        " WHERE STATUS = 'A' ");
                if (verificarQuantidadeProcessosSQL.proximo()) {
                    quantidadeExecucao = verificarQuantidadeProcessosSQL.getValorBigDecimal("QTD");
                }
                
                if (quantidadeExecucao.compareTo(BigDecimal.valueOf(40L)) < 0) {
                    ProcessamentoFilaModel.getInstance("gestor").executar();
                }
            } catch (Exception e) {
                e.printStackTrace();
                scheduledActionContext.log(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
