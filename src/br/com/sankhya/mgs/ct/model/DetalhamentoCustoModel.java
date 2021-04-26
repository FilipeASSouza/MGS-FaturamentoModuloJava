package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

/**
 * Entidade: MGSCT_Detalhamento_Custo
 * Tabela: MGSTCTEVTMENSAL
 * Chave: NUEVTMENSAL
 */
public class DetalhamentoCustoModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Detalhamento_Custo");
    private DynamicVO vo;
    private BigDecimal valorTotalEvento;
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

        if( vo.asBigDecimal("CODPRONTUARIO") != null ){
            DynamicVO custoEmpregado = daoRH.findOne("MATRICULA = ?", new Object[]{vo.asBigDecimal("CODPRONTUARIO")});
            if( custoEmpregado.asBigDecimal("MATRICULA") != null ){
                vo.setProperty("NOME", custoEmpregado.asString("NOME"));
            }
        }

        NativeSqlDecorator consultaCustoFaturaSQL = new NativeSqlDecorator("select codcusto, codtipofatura from mgstctevtcus where codevento = :codevento and ROWNUM < 2");
        consultaCustoFaturaSQL.setParametro("codevento", vo.asBigDecimal("CODEVENTO"));
        if(consultaCustoFaturaSQL.proximo()){
            vo.setProperty("CODCUSTO", consultaCustoFaturaSQL.getValorBigDecimal("CODCUSTO"));
            vo.setProperty("CODTIPOFATURA", consultaCustoFaturaSQL.getValorBigDecimal("CODTIPOFATURA"));
        }
    }

    public void validaDadosUpdate() throws Exception {

        JapeWrapper daoRH = JapeFactory.dao("MGSCT_Empregado_RH");

        if( vo.asBigDecimal("CODPRONTUARIO") != null ){
            DynamicVO custoEmpregado = daoRH.findOne("MATRICULA = ?", new Object[]{vo.asBigDecimal("CODPRONTUARIO")});
            if( custoEmpregado.asBigDecimal("MATRICULA") != null ){
                vo.setProperty("NOME", custoEmpregado.asString("NOME"));
            }
        }
    }
    //inutilizado
    public void validaDadosModificados(ModifingFields persistenceEvent) throws Exception {

        if( vo.asBigDecimal("CODINTEGRACAOLC") != null ){
            ErroUtils.disparaErro("Alteração não permitida, registro já vinculado a uma planilha!");
        }
    }

    private void validaDadosUpdate(DynamicVO oldvo) throws Exception {

    }

    public void preencheCamposCalculados() throws Exception {

        vo.setProperty("TIPLANCEVENTO","M");
        vo.setProperty("DHINS", TimeUtils.getNow());
        vo.setProperty("USUINS", JapeFactory.dao("Usuario").findByPK(AuthenticationInfo.getCurrent().getUserID()).asString("NOMEUSU"));

        if( vo.asBigDecimal("CODTIPOPOSTO") != null ){

            BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
            BigDecimal valorUnitario = getPrecoEvento();

            valorTotalEvento = quantidade.multiply(valorUnitario);
            vo.setProperty("VLRUNIEVENTO", valorUnitario);

            if( vo.asString("CALCULATXMANUAL") == null
                    || vo.asString("CALCULATXMANUAL").equalsIgnoreCase(String.valueOf("N")) ) {
                vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2, RoundingMode.UP ));
            }else{
                vo.setProperty("VLRTOTEVENTO", valorTotalEvento);
                calculaTaxaManual();
            }
        }else if( vo.asBigDecimal("CODSERVMATERIAL") != null ){
            if( vo.asBigDecimal("QTDEVENTO") != null
                    && vo.asBigDecimal("VLRUNIEVENTO") != null ){
                BigDecimal valorUnitario = vo.asBigDecimal("VLRUNIEVENTO");
                BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
                valorTotalEvento = quantidade.multiply(valorUnitario);

                if( vo.asString("CALCULATXMANUAL") == null
                        || vo.asString("CALCULATXMANUAL").equalsIgnoreCase(String.valueOf("N")) ) {
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2, RoundingMode.UP) );
                }else{
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento);
                    calculaTaxaManual();
                }
            }else{
                BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
                BigDecimal valorUnitario = getPrecoEvento();

                valorTotalEvento = quantidade.multiply(valorUnitario);
                vo.setProperty("VLRUNIEVENTO", valorUnitario);

                if( vo.asString("CALCULATXMANUAL") == null
                    || vo.asString("CALCULATXMANUAL").equalsIgnoreCase(String.valueOf("N")) ) {
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento);
                }else{
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento);
                    calculaTaxaManual();
                }
            }
        }
        vo.setProperty("COMPEVENTO", vo.asBigDecimal("COMPLANC"));
    }

    private BigDecimal getPrecoEvento() throws Exception {
        BigDecimal valorUnitario;
        BigDecimal numeroModalidade = null;

        NativeSqlDecorator modalidadeSQL = new NativeSqlDecorator("SELECT CODTPN FROM MGSTCTMODALCONTR WHERE NUMODALIDADE = :NUMODALIDADE");
        modalidadeSQL.setParametro("NUMODALIDADE", vo.asBigDecimal("NUMODALIDADE"));

        if(modalidadeSQL.proximo()){
            numeroModalidade = modalidadeSQL.getValorBigDecimal("CODTPN");
        }

        JapeWrapper mgsct_valores_eventosDAO = JapeFactory.dao("MGSCT_Valores_Eventos");
        NativeSqlDecorator nativeSqlDDecorator = new NativeSqlDecorator(this, "sql/BuscaNumeroUnicoPrecoPosto.sql");
        nativeSqlDDecorator.setParametro("NUMCONTRATO", vo.asBigDecimal("NUMCONTRATO"));
        nativeSqlDDecorator.setParametro("CODTPN", numeroModalidade );
        nativeSqlDDecorator.setParametro("CODTIPOPOSTO", vo.asBigDecimal("CODTIPOPOSTO"));
        nativeSqlDDecorator.setParametro("CODEVENTO", vo.asBigDecimal("CODEVENTO"));

        BigDecimal numeroUnicoValoresEventos = BigDecimal.ZERO;
        if (nativeSqlDDecorator.proximo()) {
            numeroUnicoValoresEventos = nativeSqlDDecorator.getValorBigDecimal("NUCONTREVENTO");
            if (numeroUnicoValoresEventos == null) {
                numeroUnicoValoresEventos = BigDecimal.ZERO;
            }
        }
        if (BigDecimal.ZERO.equals(numeroUnicoValoresEventos)) {
            ErroUtils.disparaErro("Preço não localizado, favor verificar dados lancados!");
        }

        DynamicVO mgsct_valores_eventosVO = mgsct_valores_eventosDAO.findByPK(numeroUnicoValoresEventos);
        if (mgsct_valores_eventosVO == null) {
            ErroUtils.disparaErro("Preço não localizado, favor verificar dados lancados!");
        }

        valorUnitario = mgsct_valores_eventosVO.asBigDecimal("VLRTOTAL");
        return valorUnitario;
    }

    public void recalculaCamposCalculados() throws Exception {

        vo.setProperty("DHUPD", TimeUtils.getNow());
        vo.setProperty("USUUPD", JapeFactory.dao("Usuario").findByPK(AuthenticationInfo.getCurrent().getUserID()).asString("NOMEUSU"));

        if( vo.asBigDecimal("CODTIPOPOSTO") != null ){

            BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
            BigDecimal valorUnitario = getPrecoEvento();

            valorTotalEvento = quantidade.multiply(valorUnitario);
            vo.setProperty("VLRUNIEVENTO", valorUnitario);

            if( vo.asString("CALCULATXMANUAL") == null
                || vo.asString("CALCULATXMANUAL").equalsIgnoreCase(String.valueOf("N")) ) {
                vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2,RoundingMode.UP));
            }else{
                vo.setProperty("VLRTOTEVENTO", valorTotalEvento);
                calculaTaxaManual();
            }
        }else if( vo.asBigDecimal("CODSERVMATERIAL") != null ){
            if( vo.asBigDecimal("QTDEVENTO") != null
                    && vo.asBigDecimal("VLRUNIEVENTO") != null ){

                BigDecimal valorUnitario = vo.asBigDecimal("VLRUNIEVENTO");
                BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
                valorTotalEvento = quantidade.multiply(valorUnitario);

                if( vo.asString("CALCULATXMANUAL") == null
                    || vo.asString("CALCULATXMANUAL").equalsIgnoreCase(String.valueOf("N"))) {
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2,RoundingMode.UP ));
                }else{
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento);
                    calculaTaxaManual();
                }
            }else{
                BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
                BigDecimal valorUnitario = getPrecoEvento();

                valorTotalEvento = quantidade.multiply(valorUnitario);
                vo.setProperty("VLRUNIEVENTO", valorUnitario);

                if( vo.asString("CALCULATXMANUAL") == null
                    || vo.asString("CALCULATXMANUAL").equalsIgnoreCase(String.valueOf("N")) ) {
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento);
                }else{
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento);
                    calculaTaxaManual();
                }
            }
        }
        vo.setProperty("COMPEVENTO", vo.asBigDecimal("COMPLANC"));
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

        BigDecimal valortotal = null;

        JapeWrapper eventoCustoDAO = JapeFactory.dao("MGSCT_Eventos_Custos");
        DynamicVO eventoCustoVO = eventoCustoDAO.findOne("CODEVENTO = ? ", new Object[]{vo.asBigDecimal("CODEVENTO")});

        NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator(this, "DetalhamentoCustoCalculaTaxaManual.sql");
        nativeSqlDecorator.setParametro("UNIDADEFATURAMENTO",vo.asBigDecimal("CODUNIDADEFATUR"));
        nativeSqlDecorator.setParametro("CODTIPOFATURA",eventoCustoVO.asBigDecimal("CODTIPOFATURA"));
        nativeSqlDecorator.setParametro("VALOR_DIGITADO",valorTotalEvento);

        if( nativeSqlDecorator.proximo() ){
            valortotal = nativeSqlDecorator.getValorBigDecimal("VALOR");
        }

        vo.setProperty("VLRTOTEVENTO", valortotal.setScale(2, RoundingMode.UP));
    }
}
