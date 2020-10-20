package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Entidade: AD_CTCONTRVLRPS
 * Tabela: AD_CTCONTRVLRPS
 * Chave: SEQCONTREVENTO
 */
public class ValoresEventosModel {
    private JapeWrapper dao = JapeFactory.dao("AD_CTCONTRVLRPS");
    private DynamicVO vo;
    public ValoresEventosModel()  {
    }

    public ValoresEventosModel(BigDecimal numeroUnico) throws Exception {//Chave: SEQCONTREVENTO
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    public ValoresEventosModel(DynamicVO dynamicVO) throws Exception {
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

        if(vo.asTimestamp("DTINCLUSAO") == null) {
            vo.setProperty("DTINCLUSAO", vo.asTimestamp("DTALTER"));
        }
    }

    private void validaDadosUpdate() throws Exception {

    }

    private void validaDadosUpdate(DynamicVO oldvo) throws Exception {

    }

    private void preecheCamposCalculados() throws Exception {

    }

    private void criaRegistrosDerivados() throws Exception {

    }

    private void validaDelete() throws Exception {
        
    }

    /*  consulta para pegar campo que nao pode ser alterado com uma descricao correta
select 'if (campos.containsKey("'||NOMECAMPO||'")) {mensagemErro += "Campo '||DESCRCAMPO||' não pode ser modificado. ";}' from tddcam where nometab = 'TABELA'  and nomecampo NOT IN ('CAMPO1','CAMPO2') order by ordem 
    */

    private void validaCamposUpdate(HashMap<String, Object[]> campos) throws Exception {
        String mensagemErro = "";


        if (campos.containsKey("#CAMPO#")) {
            mensagemErro += "Campo Evento não pode ser modificado. ";
        }

        if (mensagemErro != "") {
            ErroUtils.disparaErro(mensagemErro);
        }
    }
}
