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
import java.sql.Timestamp;
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
    private String verificarTaxa;
    private BigDecimal valorUnitario;
    private BigDecimal valorUnitarioTemporario;

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

        //Correção para não ter problema ao duplicar os registros
        vo.setProperty("CODINTEGRACAOLC", null);
        vo.setProperty("NULCTCUSTO", null);
        vo.setProperty("DHUPD", null);
        vo.setProperty("USUUPD", null);

        /* verificar com o Juliano depois esses dois campos */
        //vo.setProperty("CODCARGA", null);
        //vo.setProperty("MTVCARGA", null);

        vo.setProperty("CODINTEGRACAODC", null);

        JapeWrapper daoRH = JapeFactory.dao("MGSCT_Empregado_RH");

        BigDecimal anoMes = TimeUtils.getYearMonth(vo.asTimestamp("DTLCCUSTO"));
        BigDecimal compentenciaFaturamento = vo.asBigDecimal("COMPFATU").setScale(0,RoundingMode.UNNECESSARY);

        if( !anoMes.equals( compentenciaFaturamento ) ){
            ErroUtils.disparaErro("Competencia do faturamento diferente da data do lançamento!");
        }

        if( vo.asBigDecimal("CODTIPOPOSTO") == null
            && vo.asBigDecimal("CODSERVMATERIAL") == null ){
            ErroUtils.disparaErro("Necessário preencher o posto ou o serviço, fineza verificar!");
        }

        if( vo.asBigDecimalOrZero("CODCARGA") == null ) {
            if( vo.asBigDecimal("CODPRONTUARIO") != null ){
                DynamicVO custoEmpregado = daoRH.findOne("MATRICULA = ?", new Object[]{vo.asBigDecimal("CODPRONTUARIO")});
                if( custoEmpregado.asBigDecimal("MATRICULA") != null ){
                    vo.setProperty("NOME", custoEmpregado.asString("NOME"));
                }
            }
        }

        NativeSqlDecorator consultaCustoFaturaSQL = new NativeSqlDecorator("select codcusto, codtipofatura from mgstctevtcus where codevento = :CODEVENTO and ROWNUM < 2 " +
                "AND NVL(\n" +
                "  (SELECT MAX(MX.NUMCONTRATO)\n" +
                "  FROM MGSTCTEVTCUS MX\n" +
                "  WHERE MX.CODEVENTO = :CODEVENTO\n" +
                "  AND MX.NUMCONTRATO = :NUMCONTRATO\n" +
                "  ),9)               = NVL(MGSTCTEVTCUS.NUMCONTRATO,9)");
        consultaCustoFaturaSQL.setParametro("CODEVENTO", vo.asBigDecimal("CODEVENTO"));
        consultaCustoFaturaSQL.setParametro("NUMCONTRATO", vo.asBigDecimal("NUMCONTRATO"));
        if(consultaCustoFaturaSQL.proximo()){
            vo.setProperty("CODCUSTO", consultaCustoFaturaSQL.getValorBigDecimal("CODCUSTO"));
            vo.setProperty("CODTIPOFATURA", consultaCustoFaturaSQL.getValorBigDecimal("CODTIPOFATURA"));
        }

        // Juliano
        verificarRegistroDuplicado(vo.asBigDecimal("CODUNIDADEFATUR"), vo.asBigDecimal("CODTIPOFATURA"), vo.asTimestamp("DTLCCUSTO"));

        if( vo.asBigDecimal("CODPRONTUARIO") != null ){
            BigDecimal codigoCargo = null;

            NativeSqlDecorator consultarCargo = new NativeSqlDecorator("SELECT CODCARGO + 0 AS CODCARGO FROM mgsvctempregadorh where MATRICULA = :MATRICULA");
            consultarCargo.setParametro("MATRICULA", vo.asBigDecimal("CODPRONTUARIO"));
            if(consultarCargo.proximo()){
                codigoCargo = consultarCargo.getValorBigDecimal("CODCARGO");
            }
            vo.setProperty("CODCARGO", codigoCargo);
        }
    }

    public void validaDadosUpdate() throws Exception {

        //Correção para não ter problema ao duplicar os registros
        vo.setProperty("CODINTEGRACAOLC", null);
        vo.setProperty("NULCTCUSTO", null);

        BigDecimal anoMes = TimeUtils.getYearMonth(vo.asTimestamp("DTLCCUSTO"));
        BigDecimal compentenciaFaturamento = vo.asBigDecimal("COMPFATU");

        if( !anoMes.equals( compentenciaFaturamento ) ){
            ErroUtils.disparaErro("Competencia do faturamento diferente da data do lançamento!");
        }

        NativeSqlDecorator consultaCustoFaturaSQL = new NativeSqlDecorator("select codcusto, codtipofatura from mgstctevtcus where codevento = :CODEVENTO and ROWNUM < 2 " +
                "AND NVL(\n" +
                "  (SELECT MAX(MX.NUMCONTRATO)\n" +
                "  FROM MGSTCTEVTCUS MX\n" +
                "  WHERE MX.CODEVENTO = :CODEVENTO\n" +
                "  AND MX.NUMCONTRATO = :NUMCONTRATO\n" +
                "  ),9)               = NVL(MGSTCTEVTCUS.NUMCONTRATO,9)");
        consultaCustoFaturaSQL.setParametro("CODEVENTO", vo.asBigDecimal("CODEVENTO"));
        consultaCustoFaturaSQL.setParametro("NUMCONTRATO", vo.asBigDecimal("NUMCONTRATO"));
        if(consultaCustoFaturaSQL.proximo()){
            vo.setProperty("CODCUSTO", consultaCustoFaturaSQL.getValorBigDecimal("CODCUSTO"));
            vo.setProperty("CODTIPOFATURA", consultaCustoFaturaSQL.getValorBigDecimal("CODTIPOFATURA"));
        }

        JapeWrapper daoRH = JapeFactory.dao("MGSCT_Empregado_RH");

        // Juliano
        verificarRegistroDuplicado(vo.asBigDecimal("CODUNIDADEFATUR"), vo.asBigDecimal("CODTIPOFATURA"), vo.asTimestamp("DTLCCUSTO"));

        if( vo.asBigDecimal("CODTIPOPOSTO") == null
                && vo.asBigDecimal("CODSERVMATERIAL") == null ){
            ErroUtils.disparaErro("Necessário preencher o posto ou o serviço, fineza verificar!");
        }

        if( vo.asBigDecimal("CODPRONTUARIO") != null ){
            DynamicVO custoEmpregado = daoRH.findOne("MATRICULA = ?", new Object[]{vo.asBigDecimal("CODPRONTUARIO")});
            if( custoEmpregado.asBigDecimal("MATRICULA") != null ){
                vo.setProperty("NOME", custoEmpregado.asString("NOME"));
            }
        }

        if( vo.asString("TIPLANCEVENTO") == null
        || vo.asString("TIPLANCEVENTO").equalsIgnoreCase(String.valueOf("A"))){
            vo.setProperty("TIPLANCEVENTO", "M");
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

        if( vo.asBigDecimal("CODCARGA") != null ){
            BigDecimal valorUnitario = vo.asBigDecimal("VLRUNIEVENTO");
            BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
            valorTotalEvento = quantidade.multiply(valorUnitario);
            vo.setProperty("CALCULATXMANUAL", String.valueOf("N"));
            vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2,RoundingMode.HALF_EVEN ));
        }else {
            calcularValorPosto();
            calcularValorServico();
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
        nativeSqlDDecorator.setParametro("v_codunidadefatura", vo.asBigDecimal("CODUNIDADEFATUR"));
        nativeSqlDDecorator.setParametro("v_codtipofatura", vo.asBigDecimal("CODTIPOFATURA"));

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

    private BigDecimal getPrecoServico() throws Exception {
        BigDecimal valorUnitario;
        BigDecimal numeroModalidade = null;

        NativeSqlDecorator modalidadeSQL = new NativeSqlDecorator("SELECT CODTPN FROM MGSTCTMODALCONTR WHERE NUMODALIDADE = :NUMODALIDADE");
        modalidadeSQL.setParametro("NUMODALIDADE", vo.asBigDecimal("NUMODALIDADE"));

        if(modalidadeSQL.proximo()){
            numeroModalidade = modalidadeSQL.getValorBigDecimal("CODTPN");
        }

        JapeWrapper mgsct_valores_produtosDAO = JapeFactory.dao("MGSCT_Valores_Produtos");
        NativeSqlDecorator nativeSqlDDecorator = new NativeSqlDecorator(this, "sql/BuscaNumeroUnicoPrecoServicoMaterial.sql");
        nativeSqlDDecorator.setParametro("NUMCONTRATO", vo.asBigDecimal("NUMCONTRATO"));
        nativeSqlDDecorator.setParametro("CODTPN", numeroModalidade );
        nativeSqlDDecorator.setParametro("CODSERVMATERIAL", vo.asBigDecimal("CODSERVMATERIAL"));
        nativeSqlDDecorator.setParametro("CODEVENTO", vo.asBigDecimal("CODEVENTO"));
        nativeSqlDDecorator.setParametro("v_codunidadefatura", vo.asBigDecimal("CODUNIDADEFATUR"));
        nativeSqlDDecorator.setParametro("v_codtipofatura", vo.asBigDecimal("CODTIPOFATURA"));

        BigDecimal numeroUnicoValoresProdutos = BigDecimal.ZERO;
        if (nativeSqlDDecorator.proximo()) {
            numeroUnicoValoresProdutos = nativeSqlDDecorator.getValorBigDecimal("NUCONTRMATSRV");
            if (numeroUnicoValoresProdutos == null) {
                numeroUnicoValoresProdutos = BigDecimal.ZERO;
            }
        }

        if (BigDecimal.ZERO.equals(numeroUnicoValoresProdutos)) {
            ErroUtils.disparaErro("Preço não localizado, favor verificar dados lancados!");
        }

        DynamicVO mgsct_valores_produtosVO = mgsct_valores_produtosDAO.findByPK(numeroUnicoValoresProdutos);
        if (mgsct_valores_produtosVO == null) {
            ErroUtils.disparaErro("Preço não localizado, favor verificar dados lancados!");
        }

        valorUnitario = mgsct_valores_produtosVO.asBigDecimal("VLRTOTAL");
        return valorUnitario;
    }

    public void recalculaCamposCalculados() throws Exception {

        vo.setProperty("DHUPD", TimeUtils.getNow());
        vo.setProperty("USUUPD", JapeFactory.dao("Usuario").findByPK(AuthenticationInfo.getCurrent().getUserID()).asString("NOMEUSU"));

        calcularValorPostoAtualizacao();
        calcularValorServicoAtualizacao();

        vo.setProperty("COMPEVENTO", vo.asBigDecimal("COMPLANC"));
    }

    public void calcularValorPosto() throws Exception{

        if( vo.asBigDecimal("CODTIPOPOSTO") != null ){

            opcaoCalculaTaxa();

            if(vo.asBigDecimal("QTDEVENTO") != null
                    && vo.asBigDecimal("VLRUNIEVENTO") != null){

                this.valorUnitario = vo.asBigDecimal("VLRUNIEVENTO");
                this.valorUnitarioTemporario = this.valorUnitario;
                BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
                valorTotalEvento = quantidade.multiply(valorUnitario);

                if( this.verificarTaxa == null
                        || this.verificarTaxa.equalsIgnoreCase(String.valueOf("N"))) {
                    vo.setProperty("CALCULATXMANUAL", this.verificarTaxa );
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2, RoundingMode.HALF_EVEN));
                    calculaTaxaManual();
                }else{
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2,RoundingMode.UP ));
                    vo.setProperty("CALCULATXMANUAL", this.verificarTaxa);
                }
            }else{

                BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
                BigDecimal valorUnitario = getPrecoEvento();
                BigDecimal valorTotalUnitario = valorUnitario.divide(calcularQuantidadeDias(vo.asBigDecimal("NUMCONTRATO"), vo.asBigDecimal("CODEVENTO")), 15, RoundingMode.HALF_EVEN);

                valorTotalEvento = quantidade.multiply(valorTotalUnitario);
                vo.setProperty("VLRUNIEVENTO", valorTotalUnitario);
                vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2,RoundingMode.HALF_EVEN));

            }
        }
    }

    public void calcularValorPostoAtualizacao() throws Exception{

        if( vo.asBigDecimal("CODTIPOPOSTO") != null ){

            opcaoCalculaTaxa();

            if(vo.asBigDecimal("QTDEVENTO") != null
                    && vo.asBigDecimal("VLRUNIEVENTO") != null){

                this.valorUnitario = vo.asBigDecimal("VLRUNIEVENTO");
                this.valorUnitarioTemporario = this.valorUnitario;
                BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
                valorTotalEvento = quantidade.multiply(valorUnitario);

                if( this.verificarTaxa == null
                        || this.verificarTaxa.equalsIgnoreCase(String.valueOf("N"))) {
                    vo.setProperty("CALCULATXMANUAL", this.verificarTaxa );
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2, RoundingMode.HALF_EVEN));
                    //calculaTaxaManual();
                }else{
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2,RoundingMode.UP ));
                    vo.setProperty("CALCULATXMANUAL", this.verificarTaxa);
                }
            }else{

                BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
                BigDecimal valorUnitario = getPrecoEvento();
                BigDecimal valorTotalUnitario = valorUnitario.divide(calcularQuantidadeDias(vo.asBigDecimal("NUMCONTRATO"), vo.asBigDecimal("CODEVENTO")), 15, RoundingMode.HALF_EVEN);

                valorTotalEvento = quantidade.multiply(valorTotalUnitario);
                vo.setProperty("VLRUNIEVENTO", valorTotalUnitario);
                vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2,RoundingMode.HALF_EVEN));

            }
        }
    }

    public void calcularValorServico() throws Exception{

        if( vo.asBigDecimal("CODSERVMATERIAL") != null ){

            opcaoCalculaTaxa();

            if( vo.asBigDecimal("QTDEVENTO") != null
                    && vo.asBigDecimal("VLRUNIEVENTO") != null ){

                this.valorUnitario = vo.asBigDecimal("VLRUNIEVENTO");
                this.valorUnitarioTemporario = this.valorUnitario;
                BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
                valorTotalEvento = quantidade.multiply(valorUnitario);

                if( this.verificarTaxa == null
                || this.verificarTaxa.equalsIgnoreCase(String.valueOf("N")) ) {
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2, RoundingMode.UP));
                    vo.setProperty("CALCULATXMANUAL", this.verificarTaxa );
                    calculaTaxaManual();
                }else{
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2,RoundingMode.UP ));
                    vo.setProperty("CALCULATXMANUAL", this.verificarTaxa );
                }
            }else{
                BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
                BigDecimal valorUnitario = getPrecoServico();
                BigDecimal valorTotalUnitario = valorUnitario.divide(calcularQuantidadeDias(vo.asBigDecimal("NUMCONTRATO"), vo.asBigDecimal("CODEVENTO")),15, RoundingMode.HALF_EVEN);

                valorTotalEvento = quantidade.multiply(valorTotalUnitario);
                vo.setProperty("VLRUNIEVENTO", valorTotalUnitario);
                vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2,RoundingMode.HALF_EVEN));

            }
        }
    }

    public void calcularValorServicoAtualizacao() throws Exception{

        if( vo.asBigDecimal("CODSERVMATERIAL") != null ){

            opcaoCalculaTaxa();

            if( vo.asBigDecimal("QTDEVENTO") != null
                    && vo.asBigDecimal("VLRUNIEVENTO") != null ){

                this.valorUnitario = vo.asBigDecimal("VLRUNIEVENTO");
                this.valorUnitarioTemporario = this.valorUnitario;
                BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
                valorTotalEvento = quantidade.multiply(valorUnitario);

                if( this.verificarTaxa == null
                        || this.verificarTaxa.equalsIgnoreCase(String.valueOf("N")) ) {
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2, RoundingMode.UP));
                    vo.setProperty("CALCULATXMANUAL", this.verificarTaxa );
                    //calculaTaxaManual();
                }else{
                    vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2,RoundingMode.UP ));
                    vo.setProperty("CALCULATXMANUAL", this.verificarTaxa );
                }
            }else{
                BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEVENTO");
                BigDecimal valorUnitario = getPrecoServico();
                BigDecimal valorTotalUnitario = valorUnitario.divide(calcularQuantidadeDias(vo.asBigDecimal("NUMCONTRATO"), vo.asBigDecimal("CODEVENTO")),15, RoundingMode.HALF_EVEN);

                valorTotalEvento = quantidade.multiply(valorTotalUnitario);
                vo.setProperty("VLRUNIEVENTO", valorTotalUnitario);
                vo.setProperty("VLRTOTEVENTO", valorTotalEvento.setScale(2,RoundingMode.HALF_EVEN));

            }
        }
    }

    public BigDecimal calcularQuantidadeDias(BigDecimal contrato, BigDecimal codigoEvento) throws Exception{
        BigDecimal resultado = null;
        NativeSqlDecorator consultarDiasSQL = new NativeSqlDecorator("select qtdevtvlr as qtd from mgstctevtapt\n" +
                "where codevento = :codevento\n" +
                "and (numcontrato = :numcontrato or numcontrato is null)\n" +
                "and rownum <= 1");
        consultarDiasSQL.setParametro("codevento", codigoEvento);
        consultarDiasSQL.setParametro("numcontrato", contrato);
        if(consultarDiasSQL.proximo()){
            resultado = consultarDiasSQL.getValorBigDecimal("qtd");
        }

        if(resultado == null){
            resultado = BigDecimal.ONE;
        }
        return resultado;
    }

    public void opcaoCalculaTaxa() throws Exception{
        NativeSqlDecorator calculandoTaxaSQL = new NativeSqlDecorator("select NVL( Mgstctcontratotaxa.Ativo, 'N' ) calcula, Mgstctcontratotaxa.Vlrtaxa , Mgstctlocaltipofat.Nulocaltipofat\n" +
                "from mgstctcontrcent\n" +
                "inner join mgstctlocalcont on mgstctlocalcont.nulocalcont = mgstctcontrcent.nulocalcont\n" +
                "inner join mgstctlocaltipofat on mgstctlocalcont.nulocalcont = Mgstctlocaltipofat.Nulocalcont\n" +
                "inner join mgstctevtcus on mgstctevtcus.codtipofatura = mgstctlocaltipofat.codtipofatura\n" +
                "AND NVL(\n" +
                "  (SELECT MAX(MX.NUMCONTRATO)\n" +
                "  FROM MGSTCTEVTCUS MX\n" +
                "  WHERE MX.CODEVENTO = :codevento\n" +
                "  AND MX.NUMCONTRATO = :contrato\n" +
                "  ),9)               = NVL(MGSTCTEVTCUS.NUMCONTRATO,9)\n" +
                "left join mgstctcontratotaxa on Mgstctcontratotaxa.Nulocaltipofat = Mgstctlocaltipofat.Nulocaltipofat\n" +
                "where mgstctcontrcent.Numcontrato = :contrato\n" +
                "and mgstctcontrcent.Codsite = :codsite\n" +
                "and mgstctevtcus.codevento = :codevento\n" +
                "AND NVL(MGSTCTCONTRATOtaxa.DTFIM,sysdate)                                       >= sysdate\n" +
                "AND NVL(MGSTCTCONTRCENT.DTfim,sysdate)                                          >= SYSDATE");
        calculandoTaxaSQL.setParametro("contrato", vo.asBigDecimal("NUMCONTRATO"));
        calculandoTaxaSQL.setParametro("codsite", vo.asBigDecimal("CODUNIDADEFATUR"));
        calculandoTaxaSQL.setParametro("codevento", vo.asBigDecimal("CODEVENTO"));
        if(calculandoTaxaSQL.proximo()){
            this.verificarTaxa = calculandoTaxaSQL.getValorString("calcula");
            if(this.verificarTaxa == null){
                this.verificarTaxa = String.valueOf("N");
            }
        }
    }

    private void criaRegistrosDerivados() throws Exception {

    }

    public void validaDelete() throws Exception {
        if( vo.asBigDecimal("NUEVTMENSAL") != null ){
            ErroUtils.disparaErro("Registro não pode ser excluido, fineza verificar!");
        }
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
        BigDecimal taxa = null;

        JapeWrapper eventoCustoDAO = JapeFactory.dao("MGSCT_Eventos_Custos");
        DynamicVO eventoCustoVO = eventoCustoDAO.findOne("CODEVENTO = ? ", new Object[]{vo.asBigDecimal("CODEVENTO")});

        NativeSqlDecorator nativeSqlDecorator = new NativeSqlDecorator(this, "DetalhamentoCustoCalculaTaxaManual.sql");
        nativeSqlDecorator.setParametro("UNIDADEFATURAMENTO",vo.asBigDecimal("CODUNIDADEFATUR"));
        nativeSqlDecorator.setParametro("CODTIPOFATURA",eventoCustoVO.asBigDecimal("CODTIPOFATURA"));
        nativeSqlDecorator.setParametro("VALOR_DIGITADO",valorTotalEvento);

        if( nativeSqlDecorator.proximo() ){
            valortotal = nativeSqlDecorator.getValorBigDecimal("VALOR");
            taxa = nativeSqlDecorator.getValorBigDecimal("TAXA");
        }

        valorUnitario = valorUnitario.multiply(taxa);
        valorUnitario = valorUnitarioTemporario.add(valorUnitario);

        vo.setProperty("VLRTOTEVENTO", valortotal.setScale(2, RoundingMode.HALF_EVEN));
        vo.setProperty("VLRUNIEVENTO", valorUnitario.setScale(4, RoundingMode.HALF_EVEN));
    }

    public void verificarRegistroDuplicado(BigDecimal codigoTipoFatur, BigDecimal codigoUnidadeFatur, Timestamp dataLancamentoCusto ) throws Exception{
        //Juliano
        NativeSqlDecorator validarInsercao = new NativeSqlDecorator("select distinct 1 from mgstctlctcusto\n" +
                "where codunidadefatur = :codunidadefatur\n" +
                "and TRUNC( dtlanccusto ) = :dtlanccusto\n" +
                "and codtipofatura = :codtipofatura");
        validarInsercao.setParametro("codunidadefatur", codigoTipoFatur);
        validarInsercao.setParametro("codtipofatura", codigoUnidadeFatur);
        validarInsercao.setParametro("dtlanccusto", dataLancamentoCusto);

        if( validarInsercao.proximo() ){
            ErroUtils.disparaErro("Já existe planilha de fiscal para esse conjunto de informações. Verifique com o faturamento!");
        }
    }
}
