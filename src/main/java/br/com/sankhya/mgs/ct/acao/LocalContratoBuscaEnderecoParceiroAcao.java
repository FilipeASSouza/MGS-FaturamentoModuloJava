package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.mgs.ct.controller.LocalContratoController;

public class LocalContratoBuscaEnderecoParceiroAcao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        new LocalContratoController().doActionBuscaEnderecoParceiro(contextoAcao);
    }
}
