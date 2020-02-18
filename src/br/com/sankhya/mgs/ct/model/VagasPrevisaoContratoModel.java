package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Entidade: MGSCT_Vagas_Previsao_Contrato
 * Tabela: MGSTCTCONTRATOVAGA
 * Chave: NUCONTRVAGA
 */

public class VagasPrevisaoContratoModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Vagas_Previsao_Contrato");
    private DynamicVO vo;
    private Boolean subtrairVagaPrevisaoContrato = false;

    public VagasPrevisaoContratoModel() {
    }

    public void setVo(DynamicVO vo) {
        this.vo = vo;
    }

    public VagasPrevisaoContratoModel(DynamicVO dynamicVO) {
        this.vo = dynamicVO;
        inicialzaVariaveis();
    }

    private void inicialzaVariaveis() {

    }

    public DynamicVO criar(BigDecimal numeroUnicoPrevisaoContrato, String codigoVaga, Timestamp dataInicio) throws Exception {
        FluidCreateVO fluidCreateVO = dao.create();
        fluidCreateVO.set("NUCONTRPREV",numeroUnicoPrevisaoContrato);
        fluidCreateVO.set("CODVAGA",codigoVaga);
        fluidCreateVO.set("DTINICIO", dataInicio);
        DynamicVO save = fluidCreateVO.save();
        return save;
    }

    public void validaDelete() throws Exception {
        ErroUtils.disparaErro("Vagas não podem ser excluidas.");
    }

    public BigDecimal quantidadeVagasCriadas(BigDecimal numeroUnicoPrevisaoContrato, String codigoVaga) throws Exception {
        Collection<DynamicVO> dynamicVOS = dao.find("NUCONTRPREV = ? AND SUBSTR(CODVAGA,1,3) = ?", numeroUnicoPrevisaoContrato, codigoVaga);
    public BigDecimal quantidadeVagasAtivas(BigDecimal numeroUnicoPrevisaoContrato, String codigoVaga) throws Exception {
        Collection<DynamicVO> dynamicVOS = dao.find("NUCONTRPREV = ? AND SUBSTR(CODVAGA,1,3) = ? AND DTFIM IS NULL", numeroUnicoPrevisaoContrato, codigoVaga);
        int size = dynamicVOS.size();
        return new BigDecimal(size);
    }

    public void validaDadosUpdate(DynamicVO oldvo) throws Exception {
       Boolean dataFimNovoPreenchido = vo.asTimestamp("DTFIM") != null;
       Boolean dataFimAntigoPreenchido = oldvo.asTimestamp("DTFIM") != null;

       if (dataFimAntigoPreenchido && !dataFimNovoPreenchido){
           ErroUtils.disparaErro("Vaga não pode ser reativada!");
       }

        if (!dataFimAntigoPreenchido && dataFimNovoPreenchido) {
            Timestamp dtfim = vo.asTimestamp("DTFIM");
            if (dtfim.compareTo(TimeUtils.getNow())>0){
                ErroUtils.disparaErro("Data de finalização da vaga não pode ser maior que hoje!");
            }

            if (vagaAtivaPrevisaoUnidade()){
                ErroUtils.disparaErro("Vaga ainda se encontra ativa na previsao da undiade, deve ser desabilitado primeiro!");
            }

            subtrairVagaPrevisaoContrato = true;
        }

    }

    public boolean vagaAtivaPrevisaoUnidade() throws Exception {
        JapeWrapper vagasPrevisaoUnidadeDAO = JapeFactory.dao("MGSCT_Vagas_Previsao_Unidade");
        DynamicVO vagasPrevisaoUnidadeVO = vagasPrevisaoUnidadeDAO.findOne("CODVAGA = ? AND DTFIM IS NULL", vo.asString("CODVAGA"));
        if (vagasPrevisaoUnidadeVO != null){
            return true;
        }

        return false;
    }

    public void alteraDadosDerivados() throws Exception {
        if(subtrairVagaPrevisaoContrato){
            PrevisoesContratoModel previsoesContratoModel = new PrevisoesContratoModel(vo.asBigDecimal("NUCONTRPREV"));
            previsoesContratoModel.diminuirUmQuantidadeContrata();
            subtrairVagaPrevisaoContrato = false;
        }
    }

    public void validaDelete() throws Exception {
        ErroUtils.disparaErro("Vaga não pode ser deletada!");
    }

    public ArrayList<DynamicVO> getVagasLivres(BigDecimal numeroUnicoPrevisaoContrato) throws Exception {
        ArrayList<DynamicVO> vagaVOs = (ArrayList<DynamicVO>) dao.find("NUCONTRPREV = ? AND DTFIM IS NULL", numeroUnicoPrevisaoContrato);
        ArrayList<DynamicVO> vagaLivresVOs = new ArrayList();
        for (DynamicVO vagaVO:vagaVOs){
            if ("N".equals(vagaVO.asString("PREVUNID"))){
                vagaLivresVOs.add(vagaVO);
            }
        }
        return  vagaLivresVOs;
    }
}
