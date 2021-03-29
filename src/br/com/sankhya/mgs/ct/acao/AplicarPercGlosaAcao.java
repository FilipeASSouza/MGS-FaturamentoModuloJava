package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
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

        BigDecimal valorTaxaAdministracao = null;
        BigDecimal valorTotalEvento = null;
        BigDecimal numeroReferenciaGlosa = null;



        BigDecimal codigoTipoPosto = null;
        if (contextoAcao.getParam("CODTIPOPOSTO") != null) {
            codigoTipoPosto = new BigDecimal(contextoAcao.getParam("CODTIPOPOSTO").toString());
        }

        BigDecimal codigoServicosMaterial = null;
        if (contextoAcao.getParam("CODSERVMATERIAL") != null) {
            codigoServicosMaterial = new BigDecimal(contextoAcao.getParam("CODSERVMATERIAL").toString());
        }

        BigDecimal codigoEvento = null;
        if (contextoAcao.getParam("CODEVENTO") != null) {
            codigoEvento = new BigDecimal(contextoAcao.getParam("CODEVENTO").toString());
        }

        BigDecimal percTotEvento = null;
        if (contextoAcao.getParam("PERCTOTEVENTO") != null) {
            percTotEvento = new BigDecimal(contextoAcao.getParam("PERCTOTEVENTO").toString());
        }

        BigDecimal percTxAdm = null;
        if (contextoAcao.getParam("PERCTXADM") != null) {
            percTxAdm = new BigDecimal(contextoAcao.getParam("PERCTXADM").toString());
        }

        Registro[] linhas = contextoAcao.getLinhas();

        if (linhas.length == 0) {
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um contrato");
        } else {
            for (Registro linha : linhas) {

                numeroReferenciaGlosa = (BigDecimal) linha.getCampo("NUEVTMENSAL");

                NativeSqlDecorator referenciaGlosaSQL = new NativeSqlDecorator("SELECT " +
                " VLRTXADM," +
                " VALOR_TOTAL " +
                " FROM MGSVCTDETALHACUSTO WHERE NUEVTMENSAL = :NUEVTMENSAL");
                referenciaGlosaSQL.setParametro("NUEVTMENSAL", numeroReferenciaGlosa );

                if( referenciaGlosaSQL.proximo() ){
                    valorTaxaAdministracao = referenciaGlosaSQL.getValorBigDecimal("VLRTXADM");
                    valorTotalEvento = referenciaGlosaSQL.getValorBigDecimal("VALOR_TOTAL");
                }

                if( contextoAcao.getParam("PERCTOTEVENTO") != null
                        && contextoAcao.getParam("PERCTXADM") != null ){

                    ErroUtils.disparaErro("Apenas um valor deve ser informado para o calculo da glosa!" +
                            "Verifique o filtro novamente!");

                }else if( valorTotalEvento != BigDecimal.ZERO
                    || valorTotalEvento != null ){

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

                }else if( valorTaxaAdministracao != BigDecimal.ZERO
                    || valorTaxaAdministracao != null ){

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
}
