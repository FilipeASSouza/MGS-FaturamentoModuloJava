package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * Entidade: MGSCT_Taxa_Contrato
 * Tabela: MGSTCTCONTRATOTAXA
 * Chave: NUCONTRTAXA
 */
public class TaxaContratoModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Taxa_Contrato");
    private DynamicVO vo;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public TaxaContratoModel()  {
    }

    public TaxaContratoModel(BigDecimal numeroUnico) throws Exception {//Chave: NUCONTRTAXA
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    public TaxaContratoModel(DynamicVO dynamicVO) throws Exception {
        this.vo = dynamicVO;
        inicialzaVariaveis();
    }

    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
        inicialzaVariaveis();
    }

    private void inicialzaVariaveis()throws Exception {

    }

    public void validaDadosInsert() throws Exception {

        validaRegistrosEmAberto();
        validaDataFimVingencia();
        validaDataFinalMenorDataInicio();
    }

    public void validaDadosUpdate() throws Exception {

        validaDataFimVingencia();
        validaDataFinalMenorDataInicio();
    }

    public void validaRegistrosEmAberto() throws Exception {

        Collection<DynamicVO> taxasVO = dao.find("NULOCALTIPOFAT = ? AND NUCONTRTAXA <> ? AND DTFIM IS NULL"
                , new Object[]{ vo.asBigDecimal("NULOCALTIPOFAT"), vo.asBigDecimalOrZero("NUCONTRTAXA") });
        for(DynamicVO taxaVO : taxasVO ){
            Boolean status = taxaVO.asBigDecimal("NUCONTRTAXA") != null;
            if(status){
                ErroUtils.disparaErro("Já existe um registro Nro.Único: "+taxaVO.asBigDecimalOrZero("NUCONTRTAXA")+" em aberto!");
            }
        }
    }

    public void validaDataFimVingencia() throws Exception {
        Collection <DynamicVO> tributosVO = dao.find("NULOCALTIPOFAT = ? AND NUCONTRTAXA <> ?"
                , new Object[]{vo.asBigDecimalOrZero("NULOCALTIPOFAT"), vo.asBigDecimal("NUCONTRTAXA")});
        for( DynamicVO tributoVO : tributosVO ){
            if( vo.asTimestamp("DTINICIO").compareTo( tributoVO.asTimestamp("DTFIM")) < 0 ){
                ErroUtils.disparaErro("Data Inicio Vigência: "+sdf.format(vo.asTimestamp("DTINICIO"))
                        +" não pode ser menor que a Data Fim Vigência: "+sdf.format(tributoVO.asTimestamp("DTFIM"))
                        +" do registro Nro. Único: "+tributoVO.asBigDecimalOrZero("NUCONTRTAXA")+"!");
            }else if( vo.asTimestamp("DTINICIO").equals( tributoVO.asTimestamp("DTFIM") ) ){
                ErroUtils.disparaErro("Data Inicio Vigência: "+sdf.format(vo.asTimestamp("DTINICIO"))
                        +" não pode ser igual a Data Fim Vigência: "+sdf.format(tributoVO.asTimestamp("DTFIM"))
                        +" do registro Nro. Ùnico: "+tributoVO.asBigDecimalOrZero("NUCONTRTAXA")+"!");
            }
        }
    }

    public void validaDataFinalMenorDataInicio() throws Exception {
        Boolean dataFim = vo.asTimestamp("DTFIM") != null;
        if(dataFim){
            if(vo.asTimestamp("DTFIM").compareTo(vo.asTimestamp("DTINICIO")) < 0){
                ErroUtils.disparaErro("Data Fim Vigência: "+sdf.format(vo.asTimestamp("DTFIM"))
                        +" não pode ser menor que a Data Inicio Vigência: "+sdf.format(vo.asTimestamp("DTINICIO"))+"!");
            }
        }
    }

    private void preencheCamposCalculados() throws Exception {

    }

    private void criaRegistrosDerivados() throws Exception {

    }

    public void validaDelete() throws Exception {
        ErroUtils.disparaErro("Registro não pode excluido!");
    }

    public Collection<DynamicVO> buscarRegistraPorNumeroUnicolocaltipofatura(BigDecimal numeroUnico) throws Exception {
        Collection<DynamicVO> dynamicVOS = dao.find("NULOCALTIPOFAT = ?", numeroUnico);
        return dynamicVOS;
    }
}
