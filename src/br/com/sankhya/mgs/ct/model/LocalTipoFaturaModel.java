package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Entidade: MGSCT_Local_Tipo_Fatura
 * Tabela: MGSTCTLOCALTIPOFAT
 * Chave: NULOCALTIPOFAT
 */
public class LocalTipoFaturaModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Local_Tipo_Fatura");
    private DynamicVO vo;
    public LocalTipoFaturaModel()  {
    }

    public LocalTipoFaturaModel(BigDecimal numeroUnico) throws Exception {//Chave: NULOCALTIPOFAT
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    public LocalTipoFaturaModel(DynamicVO dynamicVO) throws Exception {
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
        if (posuiRegistroFilho()){
            ErroUtils.disparaErro("Registro não pode ser excluido. O mesmo passui registros dependentes");
        }
    }

    public void validaUpdate(HashMap<String, Object[]> campos) throws Exception {
        if (posuiRegistroFilho()){
            ErroUtils.disparaErro("Registro não pode ser Alterado. O mesmo passui registros dependentes");
        }
    }

    private boolean posuiRegistroFilho() throws Exception {
        boolean possuiTaxaContrato =
                new TaxaContratoModel()
                .buscarRegistraPorNumeroUnicolocaltipofatura(vo.asBigDecimalOrZero("NULOCALTIPOFAT")).size() > 0;
        boolean possuiTributosContrato =
                new TributosContratoModel()
                        .buscarRegistraPorNumeroUnicolocaltipofatura(vo.asBigDecimalOrZero("NULOCALTIPOFAT")).size() > 0;

        return (possuiTaxaContrato || possuiTributosContrato);
    }
}
