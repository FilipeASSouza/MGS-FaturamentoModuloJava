package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Entidade: MGCCT_Pagamento_Faturas
 * Tabela: MGSTCTPAGAMENTO
 * Chave: NUPAGTOFATUR
 */
public class PagamentoFaturaModel {
    private JapeWrapper dao = JapeFactory.dao("MGCCT_Pagamento_Faturas");
    private DynamicVO vo;
    public PagamentoFaturaModel()  {
    }

    public PagamentoFaturaModel(BigDecimal numeroUnico) throws Exception {//Chave: NUPAGTOFATUR
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    public PagamentoFaturaModel(DynamicVO dynamicVO) throws Exception {
        this.vo = dynamicVO;
        inicialzaVariaveis();
    }

    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
        inicialzaVariaveis();
    }

    private void inicialzaVariaveis() throws Exception {

    }

    public void validaDadosInsert() throws Exception {
        BigDecimal numeroUnicoFin = vo.asBigDecimalOrZero("NUFIN");

        if( !numeroUnicoFin.equals(BigDecimal.ZERO) ){
            DynamicVO financeiroVO = dao.findOne("NUFIN =?", new Object[]{numeroUnicoFin});
            if(!financeiroVO.asBigDecimalOrZero("NUFIN").equals(BigDecimal.ZERO)){
                ErroUtils.disparaErro("Baixa duplicada");
            }
        }
    }

    private void validaDadosUpdate() { }

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


        if (campos.containsKey("#CAMPO#")) { mensagemErro += "Campo Evento não pode ser modificado. "; }

        if (mensagemErro != "") {
            ErroUtils.disparaErro(mensagemErro);
        }
    }
}
