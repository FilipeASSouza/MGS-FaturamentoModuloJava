package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.GeraFilaLancamentoCustoGestorModel;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class GeraFilaLancamentoCustoGestorAcao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0){
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um contrato");
        } else {
            Timestamp dataCusto = Timestamp.valueOf(contextoAcao.getParam("DTCUSTO").toString());

            BigDecimal codigoUnidadeFaturamento = null;
            if (contextoAcao.getParam("CODSITE") != null) {
                codigoUnidadeFaturamento = new BigDecimal(contextoAcao.getParam("CODSITE").toString());
                DynamicVO siteVO = JapeFactory.dao("Site").findByPK(codigoUnidadeFaturamento);
                if (siteVO != null){
                    if (siteVO.asString("ANALITICO") == "S"){
                        codigoUnidadeFaturamento = siteVO.asBigDecimal("CODSITEPAI");
                    }
                }
            }

            BigDecimal numeroCotrato = null;
            numeroCotrato = new BigDecimal(contextoAcao.getParam("NUMCONTRATO").toString());

            if(codigoUnidadeFaturamento == null){
                codigoUnidadeFaturamento = BigDecimal.ZERO;
            }


            for (Registro linha : linhas) {


                GeraFilaLancamentoCustoGestorModel geraFilaLancamentoCustoGestorModel = new GeraFilaLancamentoCustoGestorModel();
                geraFilaLancamentoCustoGestorModel.setNumeroContrato(numeroCotrato);
                geraFilaLancamentoCustoGestorModel.setNumeroUnicoModalidade((BigDecimal)linha.getCampo("NUMODALIDADE"));
                geraFilaLancamentoCustoGestorModel.setDataCusto(dataCusto);
                geraFilaLancamentoCustoGestorModel.setCodigoUnidadeFaturamento(codigoUnidadeFaturamento);


                geraFilaLancamentoCustoGestorModel.gerarFila();
            }
        }
    }
}
