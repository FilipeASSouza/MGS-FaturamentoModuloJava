package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.PrcContrInsCargaEvtM001;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.PrcContrInsCargaEvtM004;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.Processar;

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
        }


        return null;
    }
}
