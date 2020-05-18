package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.mgs.ct.model.ImportarEventoMensalModel;

import java.math.BigDecimal;

public class ImportarEventoMensalAcao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0){
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um contrato");
        } else {
            for (Registro linha : linhas) {
                ImportarEventoMensalModel importarEventoMensalModel = new ImportarEventoMensalModel();
                importarEventoMensalModel.setNumeroUnico((BigDecimal) linha.getCampo("NUIMPEVTMENSAL"));
                importarEventoMensalModel.setNumeroContrato((BigDecimal) linha.getCampo("NUMCONTRATO"));
                importarEventoMensalModel.setNumeroUnicoModalidade((BigDecimal) linha.getCampo("CODTPN"));
                importarEventoMensalModel.setNumeroUnicoTipoServico((BigDecimal) linha.getCampo("NUTIPOSERVICO"));
                importarEventoMensalModel.setMotivoCarga((String)contextoAcao.getParam("MOTIVOCARGA"));

                importarEventoMensalModel.importa();
            }
        }
    }
}
