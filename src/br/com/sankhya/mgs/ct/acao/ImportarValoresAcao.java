package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.mgs.ct.model.ImportarValoresModel;

import java.math.BigDecimal;

public class ImportarValoresAcao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0){
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um contrato");
        } else {
            for (Registro linha : linhas) {
                ImportarValoresModel importarValoresModel = new ImportarValoresModel();
                importarValoresModel.setNumerUnico((BigDecimal) linha.getCampo("NUIMPVLR"));
                importarValoresModel.setTipoArquivo((String) linha.getCampo("TIPOARQUIVO"));
                importarValoresModel.importa();
            }
        }
    }
}
