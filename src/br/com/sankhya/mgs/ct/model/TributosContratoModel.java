package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * Entidade: MGSCT_Tributos_Contrato
 * Tabela: MGSTCTCONTRATOTRIB
 * Chave: NUCONTRATOTRIB
 */
    public class TributosContratoModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Tributos_Contrato");
    private DynamicVO vo;
    public TributosContratoModel()  {
    }

    public TributosContratoModel(BigDecimal numeroUnico) throws Exception {//Chave: NUCONTRATOTRIB
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    public TributosContratoModel(DynamicVO dynamicVO) throws Exception {
        this.vo = dynamicVO;
        inicialzaVariaveis();
    }

    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
        inicialzaVariaveis();
    }

    private void inicialzaVariaveis()throws Exception {

    }

    private void validaDadosInsert() throws Exception {

    }

    private void validaDadosUpdate() throws Exception {

    }

    private void preecheCamposCalculados() throws Exception {

    }

    private void criaRegistrosDerivados() throws Exception {

    }

    public void validaDelete() throws Exception {
        ErroUtils.disparaErro("Registor não pode excluido!");
    }

    public Collection<DynamicVO> buscarRegistraPorNumeroUnicolocaltipofatura(BigDecimal numeroUnico) throws Exception {
        Collection<DynamicVO> dynamicVOS = dao.find("NULOCALTIPOFAT = ?", numeroUnico);
        return dynamicVOS;
    }
}
