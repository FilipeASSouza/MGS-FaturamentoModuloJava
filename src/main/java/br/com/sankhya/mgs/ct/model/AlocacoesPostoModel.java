package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import com.sankhya.util.TimeUtils;

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

        vo.setProperty("USUINS", JapeFactory.dao("Usuario").findByPK(AuthenticationInfo.getCurrent().getUserID()).asString("NOMEUSU"));
        vo.setProperty("DTINS", TimeUtils.getNow() );
        if( vo.asString("STATUSALOCACAO") == null ){
            vo.setProperty("STATUSALOCACAO", "S");
        }

        /*
            Data: 05/11/2021
            Inserindo a validação do percentual informado na previsão sejá o mesmo cadastrado no arterh
         */

        if( mestrevo.asBigDecimal("CODEVENTO").equals(BigDecimal.valueOf(-3L))
                || mestrevo.asBigDecimal("CODEVENTO").equals(BigDecimal.valueOf(-4L)) ){

            NativeSqlDecorator validarPercentualSQL = new NativeSqlDecorator("SELECT AR.C_LIVRE_VALOR01 PERC FROM CRHH.RHSEGU_RES_AREA_CONTR@DLINK_MGS RES\n" +
                    "INNER JOIN CRHH.RHSEGU_AREA_RISCO@DLINK_MGS AR ON RES.CODIGO_AREA_RISCO = AR.CODIGO\n" +
                    "WHERE RES.CODIGO_CONTRATO = LPAD(:MATRICULA,15,'0') AND ROWNUM < 2 AND\n" +
                    ":DATAREF BETWEEN RES.DATA_INI_VIGENCIA AND NVL(RES.DATA_FIM_VIGENCIA, :DATAREF )");
            validarPercentualSQL.setParametro("MATRICULA", vo.asBigDecimal("MATRICULA") );
            validarPercentualSQL.setParametro("DATAREF", vo.asTimestamp("DTINICIO") );
            if( validarPercentualSQL.proximo() ){

                if(!validarPercentualSQL.getValorBigDecimal("PERC").equals(mestrevo.asBigDecimalOrZero("PERCINSALUBRIDADE"))){
                    ErroUtils.disparaErro("O percentual de insalubridade do empregado é diferente do percentual que consta no posto de serviço!");
                }
            }
        }

        validaDataeContratoAtivo( mestrevo.asBigDecimal("NUMCONTRATO"), mestrevo.asBigDecimal("CODSITE"));

        validaDataFinalMenorQueInicial();
        validaEncavalamentoPeriodosVaga();
        validaEncavalamentoPeriodosMatricula();
        validaStatusContratacaoVaga();

        if( vo.asBigDecimal("MATRICULA") != null ){
            BigDecimal codigoCargo = null;

            NativeSqlDecorator consultarCargo = new NativeSqlDecorator("SELECT CODCARGO + 0 AS CODCARGO FROM mgsvctempregadorh where MATRICULA = :MATRICULA");
            consultarCargo.setParametro("MATRICULA", vo.asBigDecimal("MATRICULA"));
            if(consultarCargo.proximo()){
                codigoCargo = consultarCargo.getValorBigDecimal("CODCARGO");
            }
            vo.setProperty("CODCARGO", codigoCargo);
        }
    }

    public void validaDadosUpdate() throws Exception {
        validaDataFinalMenorQueInicial();
        validaDataeContratoAtivo( mestrevo.asBigDecimal("NUMCONTRATO"), mestrevo.asBigDecimal("CODSITE"));

        vo.setProperty("USUUPD", JapeFactory.dao("Usuario").findByPK(AuthenticationInfo.getCurrent().getUserID()).asString("NOMEUSU"));
        vo.setProperty("DHUPD", TimeUtils.getNow() );

        if( vo.asString("STATUSALOCACAO").equalsIgnoreCase("N")){
            if( vo.asTimestamp("DTFIM") == null ){
                vo.setProperty("DTFIM", vo.asTimestamp("DTINICIO"));
            }
            vo.setProperty("STATUSALOCACAO", "N");
        }else{
            validaEncavalamentoPeriodosVaga();
            validaEncavalamentoPeriodosMatricula();
            validaStatusContratacaoVaga();
        }
    }

    private void validaDataFinalMenorQueInicial() throws Exception {
        Boolean dataFimPreenchido = vo.asTimestamp("DTFIM") != null;
        if (dataFimPreenchido){
            if (vo.asTimestamp("DTFIM").compareTo(vo.asTimestamp("DTINICIO")) < 0){
                ErroUtils.disparaErro("Data final não pode ser menor que a data incial!");
            }
        }
    }

    private void validaDataeContratoAtivo(BigDecimal numeroContrato, BigDecimal unidade ) throws Exception{
        NativeSqlDecorator validaContratoAtivo = new NativeSqlDecorator("select " +
                " codtipsituacao " +
                " from mgstctcontrato\n" +
                " where numcontrato = :numcontrato");
        validaContratoAtivo.setParametro("numcontrato", numeroContrato);
        if(validaContratoAtivo.proximo()){
            if (!validaContratoAtivo.getValorBigDecimal("codtipsituacao").equals(BigDecimal.ONE)){
                ErroUtils.disparaErro("Contrato não está ativo, fineza verificar!");
            }
        }

        Timestamp dataFimContrato = null;

        NativeSqlDecorator validaEncerramentoContrato = new NativeSqlDecorator("select " +
                " dtfim " +
                " from mgstctcontrcent\n" +
                " where codsite = :codsite");
        validaEncerramentoContrato.setParametro("codsite", unidade );
        if(validaEncerramentoContrato.proximo()){
            dataFimContrato = validaEncerramentoContrato.getValorTimestamp("dtfim");
        }

        if(dataFimContrato != null ){
            if(dataFimContrato.compareTo(TimeUtils.getNow()) < 0){
                ErroUtils.disparaErro("Unidade de faturamento encerrada, fineza verificar!");
            }
        }
    }

    private void validaDadosUpdate(DynamicVO oldvo) throws Exception {

    }

    public void preencheCamposCalculados() throws Exception {
        gravaAlocacaoPrincipal();
        gravaUnidadePrevisaoVaga();

    }

    private void gravaUnidadePrevisaoVaga() throws Exception {
        JapeWrapper vagasPrevisaoUnidadeDAO = JapeFactory.dao("MGSCT_Vagas_Previsao_Unidade");//MGSTCTUNIDPREVVAGA
        String codigoVaga = vo.asString("CODVAGA");
        DynamicVO vagasPrevisaoUnidadevo = vagasPrevisaoUnidadeDAO.findOne("CODVAGA = ? AND DTFIM IS NULL",codigoVaga);
        BigDecimal numeroUnicoPRevisaoCaga = vagasPrevisaoUnidadevo.asBigDecimal("NUUNIDPREVVAGA");
        vo.setProperty("NUUNIDPREVVAGA",numeroUnicoPRevisaoCaga);
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
                ErroUtils.disparaErro("Não é possível alocar! Necessário vinculação de um posto de serviço para esse empregado. Favor verificar!");
            }
        }
    }

    private void validaStatusContratacaoVaga() throws Exception {
        NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator("SELECT COUNT(*) AS QTD FROM MV_CONTRATACAO@DLINK_MGS WHERE STATUS_MOVIMENTACAO IN (2,3) AND \n" +
                "COD_VAGA = :CODVAGA and not exists ( select 1 from rhpess_contrato@dlink_mgs\n" +
                "                                    where rhpess_contrato.codigo+0 = :MATRICULA\n" +
                "                                    and situacao_contrato = 'D' )");
        nativeSqlDecorator.setParametro("CODVAGA", codigoVaga);
        nativeSqlDecorator.setParametro("MATRICULA", matricula);
        nativeSqlDecorator.proximo();
        Boolean vagaLivre = nativeSqlDecorator.getValorBigDecimal("QTD").equals(BigDecimal.ZERO);

        if (!vagaLivre) {
            ErroUtils.disparaErro("Vaga vinculada a uma contratacao e não pode ser alocada!");
        }

    }


}
