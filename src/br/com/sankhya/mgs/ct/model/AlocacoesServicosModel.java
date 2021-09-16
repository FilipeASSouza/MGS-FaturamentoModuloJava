package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Entidade: MGSCT_Alocacoes_Servicos
 * Tabela: MGSTCTALOCACAOSERV
 * Chave: NUALOCASERV
 */
public class AlocacoesServicosModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Alocacoes_Servicos");
    private DynamicVO vo;
    public AlocacoesServicosModel()  {
    }

    public AlocacoesServicosModel(BigDecimal numeroUnico) throws Exception {//Chave: NUALOCASERV
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    public AlocacoesServicosModel(DynamicVO dynamicVO) throws Exception {
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

    private void preencheCamposCalculados() throws Exception {

    }

    private void criaRegistrosDerivados() throws Exception {

    }

    public void validaDelete() throws Exception {
        ErroUtils.disparaErro("Registro não pode excluido!");
    }

    public void validaCamposUpdate(HashMap<String, Object[]> campos) throws Exception {
        String mensagemErro = "";

        //todo melhorar a descricao do campo pegando do dicionario de dados
        if (campos.containsKey("NUALOCASERV")) {mensagemErro += "Campo Nro. Único não pode ser modificado. ";}
        if (campos.containsKey("NUMCONTRATO")) {mensagemErro += "Campo Num. Contrato não pode ser modificado. ";}
        if (campos.containsKey("NUUNIDPREV")) {mensagemErro += "Campo Unid. Previsão não pode ser modificado. ";}
        if (campos.containsKey("QTDEALOCACAO")) {mensagemErro += "Campo Qtde Alocada não pode ser modificado. ";}
        if (campos.containsKey("VLRUNITARIO")) {mensagemErro += "Campo Valor Unitário não pode ser modificado. ";}
        if (campos.containsKey("VLRTOTAL")) {mensagemErro += "Campo Valor Total não pode ser modificado. ";}
        if (campos.containsKey("DTINS")) {mensagemErro += "Campo Dt. Inserção não pode ser modificado. ";}

        if (mensagemErro != "") {
            ErroUtils.disparaErro(mensagemErro);
        }
    }
}
