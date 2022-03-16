package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * Entidade: MGSCT_Tributos_Contrato
 * Tabela: MGSTCTCONTRATOTRIB
 * Chave: NUCONTRATOTRIB
 */
    public class TributosContratoModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Tributos_Contrato");
    private DynamicVO vo;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

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

    public void validaDadosInsert() throws Exception {

        if( vo.asString("ATIVO") == null ){
            vo.setProperty("ATIVO", "S");
        }

        if ( vo.asTimestamp("DTFIM") != null ){
                vo.setProperty("ATIVO", "N");
        }
        validaRegistroEmAberto();
        validaDataFimVingencia();
        validaDataFinalMenorDataInicio();
    }

    public void validaDadosUpdate() throws Exception {
        if( vo.asString("ATIVO") == null || !vo.asString("ATIVO").equalsIgnoreCase("N") ){
            if ( vo.asTimestamp("DTFIM") != null ){
                vo.setProperty("ATIVO", "N");
            }
        }
        validaDataFinalMenorDataInicio();
        validaDataFimVingencia();
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

    public void validaRegistroEmAberto() throws Exception {
        Collection <DynamicVO> tributosVO = dao.find("NULOCALTIPOFAT = ? AND NUCONTRATOTRIB <> ? AND DTFIM IS NULL"
                , new Object[]{vo.asBigDecimalOrZero("NULOCALTIPOFAT"), vo.asBigDecimal("NUCONTRATOTRIB")});
        for(DynamicVO tributoVO : tributosVO ){
            Boolean status = tributoVO.asBigDecimal("NUCONTRATOTRIB") != null;
            if(status){
                ErroUtils.disparaErro("Já existe o registro Nro. Único: "+tributoVO.asBigDecimalOrZero("NUCONTRATOTRIB")+" em aberto!");
            }
        }
    }

    public void validaDataFimVingencia() throws Exception {
        Collection <DynamicVO> tributosVO = dao.find("NULOCALTIPOFAT = ? AND NUCONTRATOTRIB <> ?"
                , new Object[]{vo.asBigDecimalOrZero("NULOCALTIPOFAT"), vo.asBigDecimal("NUCONTRATOTRIB")});
        for( DynamicVO tributoVO : tributosVO ){
            if( vo.asTimestamp("DTINICIO").compareTo( tributoVO.asTimestamp("DTFIM")) < 0 ){
                ErroUtils.disparaErro("Data Inicio Vigência: "+sdf.format(vo.asTimestamp("DTINICIO"))
                        +" não pode ser menor que a Data Fim Vigência: "+sdf.format(tributoVO.asTimestamp("DTFIM"))
                        +" do Nro. Único: "+tributoVO.asBigDecimalOrZero("NUCONTRATOTRIB")+"!");
            }else if( vo.asTimestamp("DTINICIO").equals( tributoVO.asTimestamp("DTFIM") ) ){
                ErroUtils.disparaErro("Data Inicio Vigência: "+sdf.format(vo.asTimestamp("DTINICIO"))
                        +" não pode ser igual a Data Fim Vigência: "+sdf.format(tributoVO.asTimestamp("DTFIM"))
                        +" do Nro. Único: "+tributoVO.asBigDecimalOrZero("NUCONTRATOTRIB")+"!");
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
