package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Entidade: MGSCT_Alocacoes_PS
 * Tabela: MGSTCTALOCACAOPS
 * Chave: NUALOCAPS
 */
public class AlocacoesPostoModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Alocacoes_PS");
    private DynamicVO vo;
    public AlocacoesPostoModel()  {
    }

    public AlocacoesPostoModel(BigDecimal numeroUnico) throws Exception {//Chave: NUALOCAPS
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    public AlocacoesPostoModel(DynamicVO dynamicVO) throws Exception {
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
        if (campos.containsKey("NUALOCAPS")) {mensagemErro += "Campo Nro. Único não pode ser modificado. ";}
        if (campos.containsKey("NUMCONTRATO")) {mensagemErro += "Campo Num. Contrato não pode ser modificado. ";}
        if (campos.containsKey("CODTIPOPOSTO")) {mensagemErro += "Campo Tipo do Posto não pode ser modificado. ";}
        if (campos.containsKey("CODCARGO")) {mensagemErro += "Campo Cargo não pode ser modificado. ";}
        if (campos.containsKey("CODEVENTO")) {mensagemErro += "Campo Evento não pode ser modificado. ";}
        if (campos.containsKey("NUUNIDPREV")) {mensagemErro += "Campo Unid. Previsão não pode ser modificado. ";}
        if (campos.containsKey("NUUNIDPREVVAGA")) {mensagemErro += "Campo Unid. Previsão Vaga não pode ser modificado. ";}
        if (campos.containsKey("MATRICULA")) {mensagemErro += "Campo Matrícula do Emrpegado não pode ser modificado. ";}
        if (campos.containsKey("DTINS")) {mensagemErro += "Campo Dt. Inserção não pode ser modificado. ";}
        if (campos.containsKey("CODVAGA")) {mensagemErro += "Campo Cód. Vaga não pode ser modificado. ";}

        if (mensagemErro != "") {
            ErroUtils.disparaErro(mensagemErro);
        }
    }
}
