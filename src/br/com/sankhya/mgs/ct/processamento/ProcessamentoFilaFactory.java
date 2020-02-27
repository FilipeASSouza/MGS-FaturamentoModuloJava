package br.com.sankhya.mgs.ct.processamento;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.Processar;
import br.com.sankhya.mgs.ct.processamento.processamentomodel.prcContrInsCargaEvtM001;

import java.math.BigDecimal;

public class ProcessamentoFilaFactory {
    public Processar getProcessamento(BigDecimal numeroUnicoTipoProcessamento) throws Exception {
        DynamicVO mgsct_tipo_processamento = JapeFactory.dao("MGSCT_Tipo_Processamento").findByPK(numeroUnicoTipoProcessamento);

        //Object o = Class.forName("pacote.pacote1.nomeDaClasse").newIstance();
        
        String nome = mgsct_tipo_processamento.asString("NOME");

        switch(nome){
            case "prcContrInsCargaEvtM001":
                return new prcContrInsCargaEvtM001();
        }


        return null;
    }
}
