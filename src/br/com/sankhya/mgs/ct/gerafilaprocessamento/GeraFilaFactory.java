package br.com.sankhya.mgs.ct.gerafilaprocessamento;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel.*;

import java.math.BigDecimal;

public class GeraFilaFactory {
    public GeraFila getGeraFila(BigDecimal numeroUnicoTipoMetrica) throws Exception {
        DynamicVO mgsct_apoio_metrica = JapeFactory.dao("MGSCT_Apoio_Metrica").findByPK(numeroUnicoTipoMetrica);

        //Object o = Class.forName("pacote.pacote1.nomeDaClasse").newIstance();
        boolean tipoMetricaGeracaoContaCorrente = "GCC".equals(mgsct_apoio_metrica.asString("TIPOMETRICA"));


        if (!tipoMetricaGeracaoContaCorrente) {
            return null;
        }


        String nome = mgsct_apoio_metrica.asString("TEXTOCHAVE");

        GeraFila geraFila = null;
        switch (nome) {
            case "CONTR_INS_CARGA_EVT_M_001":
                geraFila = new GeraFilaContrInsCargaEvtM001();
                break;
            case "CONTR_INS_CARGA_EVT_M_004":
                geraFila = new GeraFilaContrInsCargaEvtM004();
                break;
            case "CONTR_INS_CARGA_EVT_M_005":
                geraFila = new GeraFilaContrInsCargaEvtM005();
                break;
            case "CONTR_INS_CARGA_EVT_M_006":
                geraFila = new GeraFilaContrInsCargaEvtM006();
                break;
            case "CONTR_INS_CARGA_EVT_M_007":
                geraFila = new GeraFilaContrInsCargaEvtM007();
                break;
            case "CONTR_INS_CARGA_EVT_M_008":
                geraFila = new GeraFilaContrInsCargaEvtM008();
                break;
            case "CONTR_INS_CARGA_EVT_M_009":
                geraFila = new GeraFilaContrInsCargaEvtM009();
                break;
            case "CONTR_INS_CARGA_EVT_M_010":
                geraFila = new GeraFilaContrInsCargaEvtM010();
                break;
            case "CONTR_INS_CARGA_EVT_M_011":
                geraFila = new GeraFilaContrInsCargaEvtM011();
                break;
            case "CONTR_INS_CARGA_EVT_M_012":
                geraFila = new GeraFilaContrInsCargaEvtM012();
                break;
            case "CONTR_INS_CARGA_EVT_M_021":
                geraFila = new GeraFilaContrInsCargaEvtM021();
                break;
            case "CONTR_INS_CARGA_EVT_M_022":
                geraFila = new GeraFilaContrInsCargaEvtM022();
                break;
            default:
                geraFila = null;
        }

        if (geraFila!= null ){
            geraFila.setNomeProcessamento(nome);
        }
        return geraFila;
    }
}
