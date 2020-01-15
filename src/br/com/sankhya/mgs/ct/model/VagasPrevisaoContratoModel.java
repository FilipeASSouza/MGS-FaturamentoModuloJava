package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;

import java.math.BigDecimal;

/**
 * Entidade: MGSCT_Vagas_Previsao_Contrato
 * Tabela: MGSTCTCONTRATOVAGA
 */
public class VagasPrevisaoContratoModel {
    private JapeWrapper dao;
    private DynamicVO vo;

    public VagasPrevisaoContratoModel() {
        inicialzaVariaveis();
    }


    public VagasPrevisaoContratoModel(DynamicVO dynamicVO) {
        inicialzaVariaveis();
        this.vo = dynamicVO;
    }

    private void inicialzaVariaveis() {
         dao = JapeFactory.dao("MGSCT_Vagas_Previsao_Contrato");
    }

    public DynamicVO criar(BigDecimal numeroUnicoPrevisaoContrato, String codigoVaga) throws Exception {
        FluidCreateVO fluidCreateVO = dao.create();
        fluidCreateVO.set("NUCONTRPREV",numeroUnicoPrevisaoContrato);
        fluidCreateVO.set("CODVAGA",codigoVaga);
        DynamicVO save = fluidCreateVO.save();
        return save;
    }

}
