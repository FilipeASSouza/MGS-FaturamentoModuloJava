package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.GeraFilaContaCorrenteModel;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GeraFilaContaCorrenteAcao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0){
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um contrato");
        } else {
            Timestamp dataReferencia = Timestamp.valueOf(contextoAcao.getParam("DTREF").toString());
            BigDecimal codigoUnidadeFaturamentoInicial;
            if (contextoAcao.getParam("CODSITEI") != null) {
                codigoUnidadeFaturamentoInicial = new BigDecimal(contextoAcao.getParam("CODSITEI").toString());
            } else {
                codigoUnidadeFaturamentoInicial = BigDecimal.ZERO;
            }
            BigDecimal codigoUnidadeFaturamentoFinal;
            if (contextoAcao.getParam("CODSITEF") != null) {
                codigoUnidadeFaturamentoFinal = new BigDecimal(contextoAcao.getParam("CODSITEF").toString());
            } else {
                codigoUnidadeFaturamentoFinal = BigDecimal.ZERO;
            }

            BigDecimal tipoDeProcessamento;
            if (contextoAcao.getParam("NUTIPOPROC") != null) {
                tipoDeProcessamento = new BigDecimal(contextoAcao.getParam("NUTIPOPROC").toString());
            } else {
                tipoDeProcessamento = BigDecimal.ZERO;
            }
            JdbcWrapper jdbcWrapper = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            for (Registro linha : linhas) {


                GeraFilaContaCorrenteModel geraFilaContaCorrenteModel = new GeraFilaContaCorrenteModel(jdbcWrapper);
                geraFilaContaCorrenteModel.setDataReferencia(dataReferencia);
                geraFilaContaCorrenteModel.setNumeroContrato((BigDecimal)linha.getCampo("NUMCONTRATO"));
                geraFilaContaCorrenteModel.setUnidadeFaturamentoInicial(codigoUnidadeFaturamentoInicial);
                geraFilaContaCorrenteModel.setUnidadeFaturamentoFinal(codigoUnidadeFaturamentoFinal);
                geraFilaContaCorrenteModel.setTipoDeProcessamento(tipoDeProcessamento);

                geraFilaContaCorrenteModel.gerarFila();
            }
        }
    }
}
