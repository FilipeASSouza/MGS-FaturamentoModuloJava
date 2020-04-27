package br.com.sankhya.mgs.ct.model;


import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static br.com.sankhya.bh.utils.ErroUtils.disparaErro;

public class RemanejarVagaUnidadeModel {
    private BigDecimal numeroUnicoVagaPrevisaoUnidade;
    private Timestamp dataFechamentoVaga;
    private BigDecimal codigoUnidadeDestino;
    private JapeWrapper unidadesDAO = JapeFactory.dao("MGSCT_Unidades");
    private JapeWrapper previsoesUnidadeDAO = JapeFactory.dao("MGSCT_Previsoes_Unidade");
    private JapeWrapper vagasPrevisaoUnidadeDAO = JapeFactory.dao("MGSCT_Vagas_Previsao_Unidade");
    private DynamicVO unidadesOrigemVO;
    private DynamicVO unidadesDestinoVO;
    private DynamicVO previsoesUnidadeOrigemVO;
    private DynamicVO previsoesUnidadeDestinoVO;
    private DynamicVO vagasPrevisaoUnidadeOrigemVO;
    private DynamicVO vagasPrevisaoUnidadeDestinoVO;



    public void transferir() throws Exception {
        vagasPrevisaoUnidadeOrigemVO = vagasPrevisaoUnidadeDAO.findByPK(numeroUnicoVagaPrevisaoUnidade);
        previsoesUnidadeOrigemVO = vagasPrevisaoUnidadeOrigemVO.asDymamicVO("MGSCT_Previsoes_Unidade");
        unidadesOrigemVO = previsoesUnidadeOrigemVO.asDymamicVO("MGSCT_Unidades");

        unidadesDestinoVO = unidadesDAO.findOne("NUMCONTRATO = ? AND CODSITE = ? ", unidadesOrigemVO.asBigDecimal("NUMCONTRATO"), codigoUnidadeDestino);

        if (unidadesDestinoVO == null){
            disparaErro("Unidade de destino não localizada para esse contrato!!!");
        }

        NativeSqlDecorator buscaPrevisaoUnidadeDestinoNSQL = new NativeSqlDecorator(this, "RemanejarVagaUnidadeBuscaPrevisaoUnidadeDestino.sql");
        buscaPrevisaoUnidadeDestinoNSQL.setParametro("NUCONTRCENT",unidadesDestinoVO.asBigDecimalOrZero("NUCONTRCENT"));
        buscaPrevisaoUnidadeDestinoNSQL.setParametro("CODTIPOPOSTO",previsoesUnidadeOrigemVO.asBigDecimalOrZero("CODTIPOPOSTO"));
        buscaPrevisaoUnidadeDestinoNSQL.setParametro("CODSERVMATERIAL",previsoesUnidadeOrigemVO.asBigDecimalOrZero("CODSERVMATERIAL"));
        buscaPrevisaoUnidadeDestinoNSQL.setParametro("CODEVENTO",previsoesUnidadeOrigemVO.asBigDecimalOrZero("CODEVENTO"));


        if(buscaPrevisaoUnidadeDestinoNSQL.proximo()){
            previsoesUnidadeDestinoVO = previsoesUnidadeDAO.findByPK(   buscaPrevisaoUnidadeDestinoNSQL.getValorBigDecimal("NUUNIDPREV"));
        } else {
            FluidCreateVO previsoesUnidadeFCVO = previsoesUnidadeDAO.create();
            previsoesUnidadeFCVO.set("NUCONTRCENT",unidadesDestinoVO.asBigDecimalOrZero("NUCONTRCENT"));
            previsoesUnidadeFCVO.set("CODTIPOPOSTO",previsoesUnidadeOrigemVO.asBigDecimal("CODTIPOPOSTO"));
            previsoesUnidadeFCVO.set("NUMCONTRATO",previsoesUnidadeOrigemVO.asBigDecimal("NUMCONTRATO"));
            previsoesUnidadeFCVO.set("CODSERVMATERIAL",previsoesUnidadeOrigemVO.asBigDecimal("CODSERVMATERIAL"));
            previsoesUnidadeFCVO.set("CODEVENTO",previsoesUnidadeOrigemVO.asBigDecimal("CODEVENTO"));
            previsoesUnidadeFCVO.set("QTDCONTRATADA",BigDecimal.ZERO);
            previsoesUnidadeFCVO.set("CODCONTROLE",previsoesUnidadeOrigemVO.asBigDecimal("CODEVENTO"));


            previsoesUnidadeDestinoVO = previsoesUnidadeFCVO.save();
        }


        FluidUpdateVO vagasPrevisaoUnidadeOrigemFUVO = vagasPrevisaoUnidadeDAO.prepareToUpdate(vagasPrevisaoUnidadeOrigemVO);
        vagasPrevisaoUnidadeOrigemFUVO.set("DTFIM",dataFechamentoVaga);
        vagasPrevisaoUnidadeOrigemFUVO.update();

        FluidUpdateVO previsoesUnidadeDestinoFUVO = previsoesUnidadeDAO.prepareToUpdate(previsoesUnidadeDestinoVO);
        previsoesUnidadeDestinoFUVO.set("QTDCONTRATADA", previsoesUnidadeDestinoVO.asBigDecimalOrZero("QTDCONTRATADA").add(BigDecimal.ONE));
        previsoesUnidadeDestinoFUVO.update();


    }






    public void setNumeroUnicoVagaPrevisaoUnidade(BigDecimal numeroUnicoVagaPrevisaoUnidade) {
        this.numeroUnicoVagaPrevisaoUnidade = numeroUnicoVagaPrevisaoUnidade;
    }

    public void setDataFechamentoVaga(Timestamp dataFechamentoVaga) {
        this.dataFechamentoVaga = dataFechamentoVaga;
    }

    public void setCodigoUnidadeDestino(BigDecimal codigoUnidadeDestino) {
        this.codigoUnidadeDestino = codigoUnidadeDestino;
    }

}
