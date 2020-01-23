package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.util.Collection;

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
        fluidCreateVO.set("DTINICIO", TimeUtils.getNow());
        DynamicVO save = fluidCreateVO.save();
        return save;
    }

    public void validaDelete() throws Exception {
        ErroUtils.disparaErro("Vagas não podem ser excluidas.");
    }

    public BigDecimal quantidadeVagasCriadas(BigDecimal numeroUnicoPrevisaoContrato, String codigoVaga) throws Exception {
        Collection<DynamicVO> dynamicVOS = dao.find("NUCONTRPREV = ? AND SUBSTR(CODVAGA,1,3) = ?", numeroUnicoPrevisaoContrato, codigoVaga);
        int size = dynamicVOS.size();
        return new BigDecimal(size);
    }

}
