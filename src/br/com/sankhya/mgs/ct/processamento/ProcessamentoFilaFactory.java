package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.*;

import java.math.BigDecimal;

public class ProcessamentoFilaFactory {
    public Processar getProcessamento(BigDecimal numeroUnicoTipoProcessamento) throws Exception {
        DynamicVO mgsct_tipo_processamento = JapeFactory.dao("MGSCT_Tipo_Processamento").findByPK(numeroUnicoTipoProcessamento);

        //Object o = Class.forName("pacote.pacote1.nomeDaClasse").newIstance();
        
        String nome = mgsct_tipo_processamento.asString("NOME");

        switch(nome){
            case "CONTR_INS_CARGA_EVT_M_001":
                return new PrcContrInsCargaEvtM001();
            case "CONTR_INS_CARGA_EVT_M_004":
                return new PrcContrInsCargaEvtM004();
            case "CONTR_INS_CARGA_EVT_M_005":
                return new PrcContrInsCargaEvtM005();
            case "CONTR_INS_CARGA_EVT_M_006":
                return new PrcContrInsCargaEvtM006();
            case "CONTR_INS_CARGA_EVT_M_007":
                return new PrcContrInsCargaEvtM007();
            case "CONTR_INS_CARGA_EVT_M_089":
                return new PrcContrInsCargaEvtM089();
            case "CONTR_INS_CARGA_EVT_M_010":
                return new PrcContrInsCargaEvtM010();
            case "CONTR_INS_CARGA_EVT_M_011":
                return new PrcContrInsCargaEvtM011();
            case "CONTR_INS_CARGA_EVT_M_012":
                return new PrcContrInsCargaEvtM012();
            case "CONTR_INS_CARGA_EVT_M_021":
                return new PrcContrInsCargaEvtM021();
            case "CONTR_INS_CARGA_EVT_M_022":
                return new PrcContrInsCargaEvtM022();
            case "CONTR_INS_LANC_CUSTO_UP":
                return new PrcContrInsLancCustoUP();
            case "RtnContrInsLancCustoUPAnexo":
                return new RtnContrInsLancCustoUPAnexo();
            case "CONTR_INS_LANC_CUSTO_UP_GESTOR":
                return new PrcContrInsLancCustoUPGestor();
            case "RtnContrInsLancCustoUPGestorAnexo":
                return new RtnContrInsLancCustoUPGestorAnexo();
            case "CONTR_INS_CARGA_EVT_CCT":
                return new PrcContrInsCargaEvtCCT();
            case "CONTR_INS_CARGA_EVT_DV":
                return new PrcContrInsCargaEvtDV();
            case "CONTR_INS_CARGA_EVT_DV_SUB":
                return new PrcContrInsCargaEvtDVSub();
            case "CONTR_INS_CARGA_EVT_M_GAD":
                return new PrcContrInsCargaEvtMGAD();
            case "CONTR_INS_CARGA_EVT_M_MT2":
                return new PrcContrInsCargaEvtMMT2();
            case "CONTR_INS_LANC_FATURA":
                return new PrcContrInsLancFatura();
            case "CONTR_INS_LANC_FATURA_PORTAL":
                return new PrcContrInsLancFaturaPortal();
            case "RtnContrFaturaAnexo":
                return new RtnContrFaturaAnexo();
        }
        return null;
    }
}
