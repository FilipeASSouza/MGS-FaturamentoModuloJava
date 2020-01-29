package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * Entidade: MGSCT_Previsoes_Unidade
 * Tabela: MGSTCTUNIDADEPREV
 * Chave: NUUNIDPREV
 */
public class PrevisoesUnidadeModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Previsoes_Unidade");;
    private DynamicVO vo;
    private BigDecimal numeroContrato;
    public PrevisoesUnidadeModel()  {
    }

    private PrevisoesUnidadeModel(BigDecimal numeroUnico) throws Exception {//Chave: NUUNIDPREV
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    private PrevisoesUnidadeModel(DynamicVO dynamicVO) throws Exception {
        this.vo = dynamicVO;
        inicialzaVariaveis();
    }

    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
        inicialzaVariaveis();
    }

    private void inicialzaVariaveis()throws Exception {
        DynamicVO unidadeVO = JapeFactory.dao("MGSCT_Unidades").findByPK(vo.asBigDecimal("NUCONTRCENT"));
        numeroContrato = unidadeVO.asBigDecimal("NUMCONTRATO");
    }

    public void validaDadosInsert() throws Exception {
        JapeWrapper PrevisoesContratoDAO = JapeFactory.dao("MGSCT_Previsoes_Contrato");
        Collection<DynamicVO> dynamicVOS = PrevisoesContratoDAO.find("NVL(NUMCONTRATO,0) = ? AND NVL(CODTIPOPOSTO,0) = ? AND NVL(CODSERVMATERIAL,0) = ? AND NVL(CODEVENTO,0) = ?  AND NVL(CODCONTROLE,0) =?",
                numeroContrato,
                vo.asBigDecimalOrZero("CODTIPOPOSTO"),
                vo.asBigDecimalOrZero("CODSERVMATERIAL"),
                vo.asBigDecimalOrZero("CODEVENTO"),
                vo.asBigDecimalOrZero("CODCONTROLE")
        );

        if (dynamicVOS.size()==0){
            ErroUtils.disparaErro("Não foi encontrado uma provisão do contrado com os mesmos dados da previsão unidade sendo lancada!");
        }
    }

    private void validaDadosUpdate() throws Exception {

    }

    private void preecheCamposCalculados() throws Exception {

    }

    private void criaRegistrosDerivados() throws Exception {

    }
}
