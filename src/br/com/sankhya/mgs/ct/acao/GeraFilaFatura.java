package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.GeraFilaFaturaModel;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GeraFilaFatura implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Timestamp dataReferencia = Timestamp.valueOf(contextoAcao.getParam("DTREF").toString());
        Timestamp dataEmissao = Timestamp.valueOf(contextoAcao.getParam("DTEMISSAO").toString());
        Timestamp dataVencimento = Timestamp.valueOf(contextoAcao.getParam("DTVENC").toString());
        Timestamp dataCusto = Timestamp.valueOf(contextoAcao.getParam("DTCUSTO").toString());

        BigDecimal codigoTipoFatura = new BigDecimal(contextoAcao.getParam("CODTIPOFATURA").toString());

        BigDecimal codigoUnidadeFaturamentoInicial = null;
        if (contextoAcao.getParam("CODSITEI") != null) {
            codigoUnidadeFaturamentoInicial = new BigDecimal(contextoAcao.getParam("CODSITEI").toString());
        } else {
            codigoUnidadeFaturamentoInicial = BigDecimal.ZERO;
        }
        BigDecimal codigoUnidadeFaturamentoFinal = null;
        if (contextoAcao.getParam("CODSITEF") != null) {
            codigoUnidadeFaturamentoFinal = new BigDecimal(contextoAcao.getParam("CODSITEF").toString());
        } else {
            codigoUnidadeFaturamentoFinal = new BigDecimal(999999999);
        }

        String aprovadas = contextoAcao.getParam("APROVADAS").toString();


        GeraFilaFaturaModel geraFilaFaturaModel = new GeraFilaFaturaModel();

        geraFilaFaturaModel.setDataReferencia(dataReferencia);
        geraFilaFaturaModel.setDataEmissao(dataEmissao);
        geraFilaFaturaModel.setDataVencimento(dataVencimento);
        geraFilaFaturaModel.setDataCusto(dataCusto);
        geraFilaFaturaModel.setCodigoTipoFatura(codigoTipoFatura);
        geraFilaFaturaModel.setCodigoUnidadeFaturamentoInicial(codigoUnidadeFaturamentoInicial);
        geraFilaFaturaModel.setCodigoUnidadeFaturamentoFinal(codigoUnidadeFaturamentoFinal);
        geraFilaFaturaModel.setAprovadas(aprovadas);

        geraFilaFaturaModel.gerarFila();


    }
}
