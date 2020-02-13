package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;

/**
 * Entidade: MGSCT_Alocacoes_PS
 * Tabela: MGSTCTALOCACAOPS
 * Chave: NUALOCAPS
 */
public class AlocacoesPostoModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Alocacoes_PS");
    private DynamicVO vo;

    private String codigoVaga;
    private Timestamp dataInicio;
    private Timestamp dataFim;
    private String matricula;
    private String tipoPosto;
    private BigDecimal numeroUnico;

    public AlocacoesPostoModel() {
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

    private void inicialzaVariaveis() throws Exception {
        codigoVaga = vo.asString("CODVAGA");
        dataInicio = vo.asTimestamp("DTINICIO");
        dataFim = vo.asTimestamp("DTFIM");
        matricula = vo.asString("MATRICULA");
        tipoPosto = JapeFactory.dao("TGFECUS")
                .findByPK(vo.asBigDecimalOrZero("CODEVENTO"))
                .asString("TIPOPOSTO");
        numeroUnico = vo.asBigDecimalOrZero("NUALOCAPS");


    }

    public void validaDadosInsert() throws Exception {
        validaEncavalamentoPeriodosVaga();
        validaEncavalamentoPeriodosMatricula();
    }

    public void validaDadosUpdate() throws Exception {
        validaEncavalamentoPeriodosVaga();
        validaEncavalamentoPeriodosMatricula();
    }

    private void validaDadosUpdate(DynamicVO oldvo) throws Exception {

    }

    public void preencheCamposCalculados() throws Exception {
        gravaAlocacaoPrincipal();
    }

    private void recalculaCamposCalculados() throws Exception {

    }

    private void criaRegistrosDerivados() throws Exception {

    }

    public void validaDelete() throws Exception {
        ErroUtils.disparaErro("Registro não pode excluido!");
    }

    public void validaCamposUpdate(HashMap<String, Object[]> campos) throws Exception {
        String mensagemErro = "";

        //todo melhorar a descricao do campo pegando do dicionario de dados
        if (campos.containsKey("NUALOCAPS")) {
            mensagemErro += "Campo Nro. Único não pode ser modificado. ";
        }
        if (campos.containsKey("NUMCONTRATO")) {
            mensagemErro += "Campo Num. Contrato não pode ser modificado. ";
        }
        if (campos.containsKey("CODTIPOPOSTO")) {
            mensagemErro += "Campo Tipo do Posto não pode ser modificado. ";
        }
        if (campos.containsKey("CODCARGO")) {
            mensagemErro += "Campo Cargo não pode ser modificado. ";
        }
        if (campos.containsKey("CODEVENTO")) {
            mensagemErro += "Campo Evento não pode ser modificado. ";
        }
        if (campos.containsKey("NUUNIDPREV")) {
            mensagemErro += "Campo Unid. Previsão não pode ser modificado. ";
        }
        if (campos.containsKey("NUUNIDPREVVAGA")) {
            mensagemErro += "Campo Unid. Previsão Vaga não pode ser modificado. ";
        }
        if (campos.containsKey("MATRICULA")) {
            mensagemErro += "Campo Matrícula do Emrpegado não pode ser modificado. ";
        }
        if (campos.containsKey("DTINS")) {
            mensagemErro += "Campo Dt. Inserção não pode ser modificado. ";
        }
        if (campos.containsKey("CODVAGA")) {
            mensagemErro += "Campo Cód. Vaga não pode ser modificado. ";
        }

        if (mensagemErro != "") {
            ErroUtils.disparaErro(mensagemErro);
        }
    }

    private void validaEncavalamentoPeriodosVaga() throws Exception {


        String filtro = " NUALOCAPS <> ? AND CODVAGA = ?  " +
                "AND  " +
                "(TRUNC(?) BETWEEN TRUNC(DTINICIO) AND TRUNC(DTFIM) " +
                "OR " +
                "TRUNC(?) BETWEEN TRUNC(DTINICIO) AND TRUNC(DTFIM) " +
                "OR " +
                "TRUNC(DTINICIO) BETWEEN TRUNC(?) AND TRUNC(?) " +
                "OR " +
                "TRUNC(DTFIM) BETWEEN TRUNC(?) AND TRUNC(?))";


        Collection<DynamicVO> dynamicVOS = dao.find(filtro,
                numeroUnico,
                codigoVaga,
                dataInicio, dataFim,
                dataInicio, dataFim,
                dataInicio, dataFim);

        if (dynamicVOS.size() > 0) {
            ErroUtils.disparaErro("Existe conflito de periodo para essa vaga em outra alocação, favor corrigir periodo!");
        }

    }

    private void validaEncavalamentoPeriodosMatricula() throws Exception {


        String filtro = " NUALOCAPS <> ? AND MATRICULA = ?  " +
                "AND CODEVENTO IN (SELECT CODEVENTO FROM AD_TGFECUS WHERE TIPOPOSTO = ?)" +
                "AND  " +
                "(TRUNC(?) BETWEEN TRUNC(DTINICIO) AND TRUNC(DTFIM) " +
                "OR " +
                "TRUNC(?) BETWEEN TRUNC(DTINICIO) AND TRUNC(DTFIM) " +
                "OR " +
                "TRUNC(DTINICIO) BETWEEN TRUNC(?) AND TRUNC(?) " +
                "OR " +
                "TRUNC(DTFIM) BETWEEN TRUNC(?) AND TRUNC(?))";


        Collection<DynamicVO> dynamicVOS = dao.find(filtro,
                numeroUnico,
                matricula,
                tipoPosto,
                dataInicio, dataFim,
                dataInicio, dataFim,
                dataInicio, dataFim);

        if (dynamicVOS.size() > 0) {
            ErroUtils.disparaErro("Existe conflito de periodo para essa matricula em outra alocação, favor corrigir periodo!");
        }

    }

    private void gravaAlocacaoPrincipal() throws Exception {
        final String insalubridade = "IN";
        final String funscaoGratificada = "FG";

        if (tipoPosto.equals(insalubridade) || tipoPosto.equals(funscaoGratificada)) {
            String filtro = "  MATRICULA = ?  " +
                    "AND CODEVENTO IN (SELECT CODEVENTO FROM AD_TGFECUS WHERE TIPOPOSTO = 'PS')" +
                    "AND  " +
                    "(TRUNC(?) BETWEEN TRUNC(DTINICIO) AND TRUNC(DTFIM) " +
                    "OR " +
                    "TRUNC(?) BETWEEN TRUNC(DTINICIO) AND TRUNC(DTFIM) " +
                    "OR " +
                    "TRUNC(DTINICIO) BETWEEN TRUNC(?) AND TRUNC(?) " +
                    "OR " +
                    "TRUNC(DTFIM) BETWEEN TRUNC(?) AND TRUNC(?))";


            DynamicVO alocacaoPricipalVO = dao.findOne(filtro,
                    matricula,
                    tipoPosto,
                    dataInicio, dataFim,
                    dataInicio, dataFim,
                    dataInicio, dataFim);

            if (alocacaoPricipalVO != null) {
                vo.setProperty("NUALOCAPSPRINC", alocacaoPricipalVO.asBigDecimal("NUALOCAPS"));
            }

        }
    }

    private void validaStatusContratacaoVaga() throws Exception {
        NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator("SELECT COUNT(*) AS QTD FROM MV_CONTRATACAO@DLINK_MGS WHERE STATUS_MOVIMENTACAOIN (2,3) AND COD_VAGA = :CODVAGA");
        nativeSqlDecorator.setParametro("CODVAGA",codigoVaga);
        nativeSqlDecorator.proximo();
        Boolean vagaLivre  = nativeSqlDecorator.getValorBigDecimal("QTD").equals(BigDecimal.ZERO);

        if (!vagaLivre) {
            ErroUtils.disparaErro("Vaga vinculada a uma contratacao e não pode ser alocada!");
        }

    }


}
