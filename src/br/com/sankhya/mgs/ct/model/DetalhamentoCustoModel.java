package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Entidade: MGSCT_Detalhamento_Custo
 * Tabela: MGSTCTEVTMENSAL
 * Chave: NUEVTMENSAL
 */
public class DetalhamentoCustoModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Detalhamento_Custo");
    private DynamicVO vo;
    public DetalhamentoCustoModel()  {
    }


    public DetalhamentoCustoModel(BigDecimal numeroUnico) throws Exception {//Chave: NUEVTMENSAL
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    public DetalhamentoCustoModel(DynamicVO dynamicVO) throws Exception {
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
        JapeWrapper daoRH = JapeFactory.dao("MGSCT_Empregado_RH");

        DynamicVO custoEmpregado = daoRH.findOne("MATRICULA =" + vo.asBigDecimal("CODPRONTUARIO"));

        if( custoEmpregado.asBigDecimal("MATRICULA") != null ){
            vo.setProperty("NOME", custoEmpregado.asString("NOME"));
        }
    }

    public void validaDadosUpdate() throws Exception {

        JapeWrapper daoRH = JapeFactory.dao("MGSCT_Empregado_RH");

        DynamicVO custoEmpregado = daoRH.findOne("MATRICULA =" + vo.asBigDecimal("CODPRONTUARIO"));

        if( custoEmpregado.asBigDecimal("MATRICULA") != null ){
            vo.setProperty("NOME", custoEmpregado.asString("NOME"));
        }
    }

    private void validaDadosUpdate(DynamicVO oldvo) throws Exception {

    }

    public void preencheCamposCalculados() throws Exception {
        vo.setProperty("COMPEVENTO", TimeUtils.getYearMonth(vo.asTimestamp("DTCOMPEVENTO")));
        vo.setProperty("COMPFATU", TimeUtils.getYearMonth(vo.asTimestamp("DTCOMPFATU")));
        vo.setProperty("COMPLANC", TimeUtils.getYearMonth(vo.asTimestamp("DTCOMPLANC")));
        vo.setProperty("TIPLANCEVENTO","M");
        vo.setProperty("DHINS", TimeUtils.getNow());
        vo.setProperty("DHUPD", TimeUtils.getNow());
        vo.setProperty("USUINS", AuthenticationInfo.getCurrent().getUserID());
        vo.setProperty("USUUPD", AuthenticationInfo.getCurrent().getUserID());


        calculaTaxaManual();
    }


    public void recalculaCamposCalculados() {
        vo.setProperty("DHUPD", TimeUtils.getNow());
        vo.setProperty("USUUPD", AuthenticationInfo.getCurrent().getUserID());
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

    private void calculaTaxaManual() throws Exception {
        if("S".equals(vo.asString("CALCULATXMANUAL"))){
            NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator(this, "DetalhamentoCustoCalculaTaxaManual.sql");
            nativeSqlDecorator.setParametro("UNIDADEFATURAMENTO",vo.asBigDecimal("CODUNIDADEFATUR"));
            nativeSqlDecorator.setParametro("CODTIPOFATURA",vo.asBigDecimal("CODTIPOFATURA"));
            nativeSqlDecorator.setParametro("VALOR_DIGITADO",vo.asBigDecimal("VLRTOTEVENTO"));
            nativeSqlDecorator.proximo();
            vo.setProperty("VLRTOTEVENTO",nativeSqlDecorator.getValorBigDecimal("VALOR"));
        }
    }


}
