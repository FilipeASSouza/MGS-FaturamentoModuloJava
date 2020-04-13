package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.mgs.ct.model.AplicarPercGlosaModel;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class AplicarPercGlosaAcao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        BigDecimal competencia = TimeUtils.getYearMonth(Timestamp.valueOf(contextoAcao.getParam("DTCOMPETENCIA").toString()));
        Timestamp dataLancamentoCusto = Timestamp.valueOf(contextoAcao.getParam("DTLCCUSTO").toString());
        BigDecimal codigoTipoPosto = new BigDecimal(contextoAcao.getParam("CODTIPOPOSTO").toString());
        BigDecimal codigoServicosMaterial = new BigDecimal(contextoAcao.getParam("CODSERVMATERIAL").toString());
        BigDecimal codigoEvento = new BigDecimal(contextoAcao.getParam("CODEVENTO").toString());
        BigDecimal percTotEvento = new BigDecimal(contextoAcao.getParam("PERCTOTEVENTO").toString());
        BigDecimal percTxAdm = new BigDecimal(contextoAcao.getParam(" PERCTXADM").toString());

        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0){
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um contrato");
        } else {
            for (Registro linha : linhas) {

                BigDecimal numeroUnicoEventoMensal = (BigDecimal) linha.getCampo("NUEVTMENSAL");


                AplicarPercGlosaModel aplicarPercGlosaModel = new AplicarPercGlosaModel();

                aplicarPercGlosaModel.setNumeroUnicoEventoMensal(numeroUnicoEventoMensal);
                aplicarPercGlosaModel.setCompetencia(competencia);
                aplicarPercGlosaModel.setDataLancamentoCusto(dataLancamentoCusto);
                aplicarPercGlosaModel.setCodigoTipoPosto(codigoTipoPosto);
                aplicarPercGlosaModel.setCodigoServicosMaterial(codigoServicosMaterial);
                aplicarPercGlosaModel.setCodigoEvento(codigoEvento);
                aplicarPercGlosaModel.setPercTotEvento(percTotEvento);
                aplicarPercGlosaModel.setPercTxAdm(percTxAdm);
                aplicarPercGlosaModel.executar();
            }
        }
    }
}
