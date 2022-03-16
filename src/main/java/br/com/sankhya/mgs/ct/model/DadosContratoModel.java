package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Entidade: MGSCT_Dados_Contrato
 * Tabela: MGSTCTCONTRATO
 * Chave: NUMCONTRATO
 */
public class DadosContratoModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Dados_Contrato");
    private DynamicVO vo;
    String mensagemErro = null;
    public DadosContratoModel()  {
    }

    public DadosContratoModel(BigDecimal numeroUnico) throws Exception {//Chave: NUMCONTRATO
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    public DadosContratoModel(DynamicVO dynamicVO) throws Exception {
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

        // Bloqueio para o usuário não informar dt fim menor que a dt inicio do contrato
        if(vo.asTimestamp("DTFIM").compareTo(vo.asTimestamp("DTINICIO")) < 0 ){

            ErroUtils.disparaErro("Data final esta menor que a Data Inicial do Contrato! " +
                    "<br>Verifique novamente a Data Final informada!");
        }
    }

    public void validaDadosUpdate() throws Exception {

        // Bloqueio para o usuário não informar dt fim menor que a dt inicio do contrato
        if(vo.asTimestamp("DTFIM").compareTo(vo.asTimestamp("DTINICIO")) < Integer.valueOf(0)){

            mensagemErro = "<b>Data final</b> menor que a <b>Data Inicial</b> do <b>Contrato</b>! " +
                    "\nVerifique novamente as datas informadas no contrato!";

            ErroUtils.disparaErro(mensagemErro);
        }
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
