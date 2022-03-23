package br.com.sankhya.mgs.ct.acao;

import br.com.lugh.performance.PerformanceMonitor;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.model.ImportarEventoMensalModel;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;

public class ImportarEventoMensalAcao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0){
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um contrato");
        } else {
            JdbcWrapper jdbcWrapper = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            JapeSession.SessionHandle topMostHnd = JapeSession.getCurrentSession().getTopMostHandle();
            topMostHnd.setReuseJDBCConnection(true);
            for (Registro linha : linhas) {
                PerformanceMonitor.INSTANCE.measureJava("ImportarEventoMensalAcao", () -> {
                    ImportarEventoMensalModel importarEventoMensalModel = new ImportarEventoMensalModel(jdbcWrapper);
                    importarEventoMensalModel.setNumeroUnico((BigDecimal) linha.getCampo("NUIMPEVTMENSAL"));
                    importarEventoMensalModel.setNumeroContrato((BigDecimal) linha.getCampo("NUMCONTRATO"));
                    importarEventoMensalModel.setNumeroUnicoModalidade((BigDecimal) linha.getCampo("CODTPN"));
                    importarEventoMensalModel.setNumeroUnicoTipoServico((BigDecimal) linha.getCampo("NUTIPOSERVICO"));
                    importarEventoMensalModel.setMotivoCarga((String) contextoAcao.getParam("MOTIVOCARGA"));

                    importarEventoMensalModel.importa();
                });
            }
        }
    }
}
