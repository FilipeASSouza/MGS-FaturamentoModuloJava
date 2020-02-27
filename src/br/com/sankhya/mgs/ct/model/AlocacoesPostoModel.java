package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Entidade: MGSCT_Alocacoes_PS
 * Tabela: MGSTCTALOCACAOPS
 * Chave: NUALOCAPS
 */
public class AlocacoesPostoModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Alocacoes_PS");
    private DynamicVO vo;

    /**
     * Entidade: MGSCT_Previsoes_Unidade_PS
     * Tabela: MGSVCTUNIDADEPREVPS
     * Chave: NUUNIDPREV
     */
    private DynamicVO mestrevo;

    private String codigoVaga;
    private Timestamp dataInicio;
    private Timestamp dataFim;
    private BigDecimal matricula;
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
        mestrevo = JapeFactory.dao("MGSCT_Previsoes_Unidade_PS").findByPK(vo.asBigDecimal("NUUNIDPREV"));

        codigoVaga = vo.asString("CODVAGA");
        dataInicio = vo.asTimestamp("DTINICIO");
        dataFim = vo.asTimestamp("DTFIM");
        matricula = vo.asBigDecimalOrZero("MATRICULA");
        tipoPosto = JapeFactory.dao("TGFECUS")
                .findByPK(mestrevo.asBigDecimalOrZero("CODEVENTO"))
                .asString("TIPOPOSTO");
        numeroUnico = vo.asBigDecimalOrZero("NUALOCAPS");
    }

    public void validaDadosInsert() throws Exception {
        validaEncavalamentoPeriodosVaga();
        validaEncavalamentoPeriodosMatricula();
        validaStatusContratacaoVaga();
    }

    public void validaDadosUpdate() throws Exception {
        validaEncavalamentoPeriodosVaga();
        validaEncavalamentoPeriodosMatricula();
        validaStatusContratacaoVaga();
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

        String consulta = "BuscaEncavalamentoPeriodosVagaComDataFim.sql";
        if (dataFim == null) {
            consulta = "BuscaEncavalamentoPeriodosVagaSemDataFim.sql";
        }

        NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator(this, "sql/" + consulta);


        nativeSqlDecorator.setParametro("NUALOCAPS", numeroUnico);
        nativeSqlDecorator.setParametro("CODVAGA", codigoVaga);
        nativeSqlDecorator.setParametro("DTI", dataInicio);
        if (dataFim != null) {
            nativeSqlDecorator.setParametro("DTF", dataFim);
        }

        nativeSqlDecorator.proximo();


        if (nativeSqlDecorator.getValorInt("QTD") > 0) {
            ErroUtils.disparaErro("Existe conflito de periodo para essa vaga em outra alocação, favor corrigir periodo!");
        }

    }

    private void validaEncavalamentoPeriodosMatricula() throws Exception {


        String consulta = "BuscaEncavalamentoPeriodosMatriculaComDataFim.sql";
        if (dataFim == null) {
            consulta = "BuscaEncavalamentoPeriodosMatriculaSemDataFim.sql";
        }

        NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator(this, "sql/" + consulta);


        nativeSqlDecorator.setParametro("NUALOCAPS", numeroUnico);
        nativeSqlDecorator.setParametro("TIPOPOSTO", tipoPosto);
        nativeSqlDecorator.setParametro("MATRICULA", matricula);
        nativeSqlDecorator.setParametro("DTI", dataInicio);
        if (dataFim != null) {
            nativeSqlDecorator.setParametro("DTF", dataFim);
        }

        nativeSqlDecorator.proximo();


        if (nativeSqlDecorator.getValorInt("QTD") > 0) {
            ErroUtils.disparaErro("Existe conflito de periodo para essa matricula em outra alocação, favor corrigir periodo!");
        }

    }

    private void gravaAlocacaoPrincipal() throws Exception {
        final String insalubridade = "IN";
        final String funscaoGratificada = "FG";

        if (tipoPosto.equals(insalubridade) || tipoPosto.equals(funscaoGratificada)) {
            String consulta = "BuscaAlocacaoPrincipalComDataFim.sql";
            if (dataFim == null) {
                consulta = "BuscaAlocacaoPrincipalSemDataFim.sql";
            }

            NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator(this, "sql/" + consulta);


            nativeSqlDecorator.setParametro("MATRICULA", matricula);
            nativeSqlDecorator.setParametro("DTI", dataInicio);
            if (dataFim != null) {
                nativeSqlDecorator.setParametro("DTF", dataFim);
            }

            if (nativeSqlDecorator.proximo()) {
                vo.setProperty("NUALOCAPSPRINC", nativeSqlDecorator.getValorBigDecimal("NUALOCAPS"));
            } else {
                ErroUtils.disparaErro("Aloção deve possui alocação principal e a mesma não foi localizada, favor varficar!");
            }
        }
    }

    private void validaStatusContratacaoVaga() throws Exception {
        NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator("SELECT COUNT(*) AS QTD FROM MV_CONTRATACAO@DLINK_MGS WHERE STATUS_MOVIMENTACAO IN (2,3) AND COD_VAGA = :CODVAGA");
        nativeSqlDecorator.setParametro("CODVAGA", codigoVaga);
        nativeSqlDecorator.proximo();
        Boolean vagaLivre = nativeSqlDecorator.getValorBigDecimal("QTD").equals(BigDecimal.ZERO);

        if (!vagaLivre) {
            ErroUtils.disparaErro("Vaga vinculada a uma contratacao e não pode ser alocada!");
        }

    }


}
