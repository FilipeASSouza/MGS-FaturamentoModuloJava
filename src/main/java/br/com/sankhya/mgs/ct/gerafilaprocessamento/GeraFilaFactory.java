package br.com.sankhya.mgs.ct.gerafilaprocessamento;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel.*;

import java.math.BigDecimal;

public class GeraFilaFactory {
    private JdbcWrapper jdbcWrapper;

    public GeraFilaFactory(JdbcWrapper jdbc) {
        this.jdbcWrapper = jdbc;
    }

    public GeraFila getGeraFilaContaCorrente(BigDecimal numeroUnicoTipoMetrica, BigDecimal tipoDeProcessamento) throws Exception {

        System.out.println("INICIANDO GERACAO DA FILA CONTA CORRENTE");

        DynamicVO mgsct_apoio_metrica = JapeFactory.dao("MGSCT_Apoio_Metrica").findByPK(numeroUnicoTipoMetrica);

        //Object o = Class.forName("pacote.pacote1.nomeDaClasse").newIstance();
        boolean tipoMetricaGeracaoContaCorrente = "GCC".equals(mgsct_apoio_metrica.asString("TIPOMETRICA"));


        if (!tipoMetricaGeracaoContaCorrente) {
            return null;
        }


        String textochave = mgsct_apoio_metrica.asString("TEXTOCHAVE");

        if(!BigDecimal.ZERO.equals(tipoDeProcessamento)) {
            JapeWrapper tipoProcessamentoDAO = JapeFactory.dao("MGSCT_Tipo_Processamento");
            DynamicVO tipoProcessamentoVO = tipoProcessamentoDAO.findByPK(tipoDeProcessamento);
            if (tipoProcessamentoVO != null) {
                String nometipoProcessamento = tipoProcessamentoVO.asString("NOME");
                if (!nometipoProcessamento.equals(textochave)) {
                    return null;
                }
            }
        }

        return getGeraFila(textochave);
    }

    public GeraFila getGeraFila(String textoChave) {

        System.out.println(" INICIANDO SWITCH GERA FILA COM TEXTO CHAVE " + textoChave);

        GeraFila geraFila;
        switch (textoChave) {
            case "CONTR_INS_CARGA_EVT_M_001":
                geraFila = new GeraFilaContrInsCargaEvtM001(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_M_004":
                geraFila = new GeraFilaContrInsCargaEvtM004(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_M_005":
                geraFila = new GeraFilaContrInsCargaEvtM005(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_M_006":
                geraFila = new GeraFilaContrInsCargaEvtM006(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_M_007":
                geraFila = new GeraFilaContrInsCargaEvtM007(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_M_089":
                geraFila = new GeraFilaContrInsCargaEvtM089(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_M_010":
                geraFila = new GeraFilaContrInsCargaEvtM010(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_M_011":
                geraFila = new GeraFilaContrInsCargaEvtM011(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_M_012":
                geraFila = new GeraFilaContrInsCargaEvtM012(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_M_021":
                geraFila = new GeraFilaContrInsCargaEvtM021(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_M_022":
                geraFila = new GeraFilaContrInsCargaEvtM022(jdbcWrapper);
                break;
            case "CONTR_INS_LANC_CUSTO_UP":
                geraFila = new GeraFilaContrInsLancCustoUP(jdbcWrapper);
                break;
            case "RtnContrInsLancCustoUPAnexo":
                geraFila = new GeraFilaContrInsLancCustoUPAnexo(jdbcWrapper);
                break;
            case "CONTR_INS_LANC_CUSTO_UP_GESTOR":
                geraFila = new GeraFilaContrInsLancCustoUPGestor(jdbcWrapper);
                break;
            case "RtnContrInsLancCustoUPGestorAnexo":
                geraFila = new GeraFilaContrInsLancCustoUPGestorAnexo(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_CCT":
                geraFila = new GeraFilaContrInsCargaEvtCCT(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_DV":
                geraFila = new GeraFilaContrInsCargaEvtDV(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_DV_SUB":
                geraFila = new GeraFilaContrInsCargaEvtDVSub(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_M_GAD":
                geraFila = new GeraFilaContrInsCargaEvtMGAD(jdbcWrapper);
                break;
            case "CONTR_INS_CARGA_EVT_M_MT2":
                geraFila = new GeraFilaContrInsCargaEvtMMT2(jdbcWrapper);
                break;
            case "CONTR_INS_LANC_FATURA":
                geraFila = new GeraFilaContrInsLancFatura(jdbcWrapper);
                break;
            case "CONTR_INS_LANC_FATURA_PORTAL":
                geraFila = new GeraFilaContrInsLancFaturaPortal(jdbcWrapper);
                break;
            default:
                geraFila = null;
        }

        if (geraFila!= null ){
            geraFila.setParametroExecucao("nomeProcessamento",textoChave);
        }
        return geraFila;
    }
}
