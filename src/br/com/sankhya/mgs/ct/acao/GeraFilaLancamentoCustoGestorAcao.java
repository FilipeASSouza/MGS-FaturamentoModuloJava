package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.GeraFilaLancamentoCustoGestorModel;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;

public class GeraFilaLancamentoCustoGestorAcao implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0){
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um contrato");
        } else {
            Timestamp dataCusto = Timestamp.valueOf( contextoAcao.getParam("DTCUSTO").toString() );

            BigDecimal codigoUnidadeFaturamento = null;
            BigDecimal codigoUnidadeFaturamentoFinal = null;
            if (contextoAcao.getParam("CODSITE") != null || contextoAcao.getParam("CODSITEF") != null ) {
                codigoUnidadeFaturamento = new BigDecimal(contextoAcao.getParam("CODSITE").toString());
                codigoUnidadeFaturamentoFinal = new BigDecimal(contextoAcao.getParam("CODSITEF").toString());
                Collection <DynamicVO> sitesVO = JapeFactory.dao("Site").find("CODSITE BETWEEN ? AND ?"
                        , new Object[]{codigoUnidadeFaturamento, codigoUnidadeFaturamentoFinal});
                for(DynamicVO siteVO : sitesVO){
                if (siteVO != null){
                    if (siteVO.asString("ANALITICO") == "S"){
                        codigoUnidadeFaturamento = siteVO.asBigDecimal("CODSITEPAI");
                    }
                }
            }
            }

            System.out.println("INICIANDO FILA GESTOR BOTAO DE ACAO");
            JdbcWrapper jdbcWrapper = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            GeraFilaLancamentoCustoGestorModel geraFilaLancamentoCustoGestorModel = new GeraFilaLancamentoCustoGestorModel(jdbcWrapper);
            for (Registro linha : linhas) {
              
                geraFilaLancamentoCustoGestorModel.setNumeroContrato((BigDecimal)linha.getCampo("NUMCONTRATO"));
                geraFilaLancamentoCustoGestorModel.setNumeroUnicoModalidade((BigDecimal)linha.getCampo("NUMODALIDADE"));
                geraFilaLancamentoCustoGestorModel.setDataCusto( dataCusto );
                geraFilaLancamentoCustoGestorModel.setCodigoUnidadeFaturamento( codigoUnidadeFaturamento );
                geraFilaLancamentoCustoGestorModel.setCodigoUnidadeFaturamentoFinal(codigoUnidadeFaturamentoFinal);

                geraFilaLancamentoCustoGestorModel.gerarFila();
            }
        }
    }
}
