package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;

/**
 * Entidade: MGSCT_Alocacoes_Servicos
 * Tabela: MGSTCTALOCACAOSERV
 * Chave: NUALOCASERV
 */
public class AlocacoesServicosModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Alocacoes_Servicos");
    private JapeWrapper unidadePrevisaoDAO = JapeFactory.dao("MGSCT_Previsoes_Unidade");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private DynamicVO vo;
    private String regraVadalicao = "";
    private BigDecimal codigoModalidade = null;
    private BigDecimal codigoEvento = null;
    private BigDecimal numeroContrato = null;
    private BigDecimal codigoControle = null;
    private BigDecimal codigoServMaterial = null;
    private Timestamp dataInicio = null;
    private BigDecimal qtdContratada = null;
    private BigDecimal qtdalocacaoUnidade = null;
    private BigDecimal qtdTotalAlocacao = null;

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

    public void validaDadosInsert() throws Exception {

        validaRegistrosEmAberto();
        validaDataFimVingencia();
        validaDataFinalMenorDataInicio();
    }

    public void validaDadosUpdate() throws Exception {

        validaDataFimVingencia();
        validaDataFinalMenorDataInicio();

    }

    public void validaRegistrosEmAberto() throws Exception {

        Collection<DynamicVO> taxasVO = dao.find("NUUNIDPREV = ? AND NUALOCASERV <> ? AND DTFIM IS NULL"
                , new Object[]{ vo.asBigDecimal("NUUNIDPREV"), vo.asBigDecimalOrZero("NUALOCASERV") });
        for(DynamicVO taxaVO : taxasVO ){
            Boolean status = taxaVO.asBigDecimal("NUALOCASERV") != null;
            if(status){
                ErroUtils.disparaErro("J� existe um registro Nro.�nico: "+taxaVO.asBigDecimalOrZero("NUALOCASERV")+" em aberto!");
            }
        }
    }

    public void validaDataFimVingencia() throws Exception {
        Collection <DynamicVO> tributosVO = dao.find("NUUNIDPREV = ? AND NUALOCASERV <> ?"
                , new Object[]{vo.asBigDecimalOrZero("NUUNIDPREV"), vo.asBigDecimal("NUALOCASERV")});
        for( DynamicVO tributoVO : tributosVO ){
            if( vo.asTimestamp("DTINICIO").compareTo( tributoVO.asTimestamp("DTFIM")) < 0 ){
                ErroUtils.disparaErro("Data Inicio Vig�ncia: "+sdf.format(vo.asTimestamp("DTINICIO"))
                        +" n�o pode ser menor que a Data Fim Vig�ncia: "+sdf.format(tributoVO.asTimestamp("DTFIM"))
                        +" do registro Nro.�nico: "+tributoVO.asBigDecimalOrZero("NUALOCASERV")+"!");
            }else if( vo.asTimestamp("DTINICIO").equals( tributoVO.asTimestamp("DTFIM") ) ){
                ErroUtils.disparaErro("Data Inicio Vig�ncia: "+sdf.format(vo.asTimestamp("DTINICIO"))
                        +" n�o pode ser igual a Data Fim Vig�ncia: "+sdf.format(tributoVO.asTimestamp("DTFIM"))
                        +" do registro Nro.�nico: "+tributoVO.asBigDecimalOrZero("NUALOCASERV")+"!");
            }
        }
    }

    public void validaDataFinalMenorDataInicio() throws Exception {
        Boolean dataFim = vo.asTimestamp("DTFIM") != null;
        if(dataFim){
            if(vo.asTimestamp("DTFIM").compareTo(vo.asTimestamp("DTINICIO")) < 0){
                ErroUtils.disparaErro("Data Fim Vig�ncia: "+sdf.format(vo.asTimestamp("DTFIM"))
                        +" n�o pode ser menor que a Data Inicio Vig�ncia: "+sdf.format(vo.asTimestamp("DTFIM"))+"!");
            }
        }
    }

    public void preencheCamposCalculados() throws Exception {

        BigDecimal valorUnitario = vo.asBigDecimalOrZero("VLRUNITARIO");
        BigDecimal quantidade = vo.asBigDecimalOrZero("QTDEALOCACAO");

        NativeSqlDecorator previsoesUnidadeServicoSQL = new NativeSqlDecorator("SELECT CODTPN, " +
                " CODEVENTO, " +
                " NUMCONTRATO, " +
                " CODCONTROLE, " +
                " CODSERVMATERIAL, " +
                " DTINICIO, " +
                " QTDCONTRATADA " +
                " FROM MGSVCTUNIDADEPREVSERV " +
                " WHERE NUUNIDPREV = :NUUNIDPREV AND ROWNUM <= 1");
        previsoesUnidadeServicoSQL.setParametro("NUUNIDPREV", vo.asBigDecimal("NUUNIDPREV"));

        if( previsoesUnidadeServicoSQL.proximo()){
            codigoModalidade = previsoesUnidadeServicoSQL.getValorBigDecimal("CODTPN");
            codigoEvento = previsoesUnidadeServicoSQL.getValorBigDecimal("CODEVENTO");
            numeroContrato = previsoesUnidadeServicoSQL.getValorBigDecimal("NUMCONTRATO");
            codigoControle = previsoesUnidadeServicoSQL.getValorBigDecimal("CODCONTROLE");
            codigoServMaterial = previsoesUnidadeServicoSQL.getValorBigDecimal("CODSERVMATERIAL");
            dataInicio = previsoesUnidadeServicoSQL.getValorTimestamp("DTINICIO");
            qtdContratada = previsoesUnidadeServicoSQL.getValorBigDecimal("QTDCONTRATADA");
        }

        NativeSqlDecorator qtdalocadaSQL = new NativeSqlDecorator("SELECT SUM(QTDEALOCACAO) QTDEALOCACAO FROM MGSTCTALOCACAOSERV WHERE NUUNIDPREV = :NUUNIDPREV " +
                " AND ( DTFIM IS NULL OR DTFIM >= SYSDATE ) ");
        qtdalocadaSQL.setParametro("NUUNIDPREV", vo.asBigDecimal("NUUNIDPREV"));

        if(qtdalocadaSQL.proximo()){
            qtdalocacaoUnidade = qtdalocadaSQL.getValorBigDecimal("QTDEALOCACAO");
            if( qtdalocacaoUnidade == null){
                qtdalocacaoUnidade = BigDecimal.ZERO;
            }
        }

        qtdTotalAlocacao = vo.asBigDecimal("QTDEALOCACAO");

        qtdTotalAlocacao = qtdTotalAlocacao.add(qtdalocacaoUnidade);

        if ( vo.asBigDecimal("QTDEALOCACAO").compareTo(qtdContratada) > 0 ){
             ErroUtils.disparaErro("Qtde Alocada n�o pode ser maior que a Qtde. Contratada !");
        }else if(qtdTotalAlocacao.compareTo(qtdContratada) > 0){
            ErroUtils.disparaErro("Quantidade total alocada n�o pode ser maior que a quantidade Contratada !");
        }

        switch (getRegraValidacaoServico()) {
            case "P"://posto
            case "C"://contrato
            case "R"://rescisao
            case "S1"://serviceo/material controle 1
            case "S2"://serviceo/material controle 2
                if (quantidade.equals(BigDecimal.ZERO)) {
                    quantidade = BigDecimal.ONE;
                }
                break;
            case "S3"://serviceo/material controle 3
            case "S4"://serviceo/material controle 4
                valorUnitario = getPrecoServico();
                if (BigDecimal.ZERO.equals(valorUnitario)) {
                    ErroUtils.disparaErro("Pre�o de Material/Servi�o localizado n�o pode ser zero, favor verificar dados lancados!");
                }
                break;
            default:
        }

        vo.setProperty("VLRUNITARIO", valorUnitario);
        vo.setProperty("VLRTOTAL", valorUnitario.multiply(quantidade));
        //vo.setProperty("DTINICIO", dataInicio );
        vo.setProperty("STATUSALOCACAO", String.valueOf("S"));
    }

    private String getRegraValidacaoServico() throws Exception {

        DynamicVO eventoVO = JapeFactory.dao("TGFECUS").findByPK(codigoEvento);
        String tipoEvento = eventoVO.asString("TIPOEVENTO");

        if ("S".equals(tipoEvento)) {

            if (codigoControle == null) {
                ErroUtils.disparaErro("Controle deve ser preenchido para esse tipo de evento!");
            }
            regraVadalicao = tipoEvento + codigoControle.toString();
        } else {
            regraVadalicao = tipoEvento;
        }

        if (regraVadalicao == null) {
            regraVadalicao = "";
        }

        return regraVadalicao;
    }

    private BigDecimal getPrecoServico() throws Exception {
        BigDecimal valorUnitario;

        JapeWrapper mgsct_valores_produtosDAO = JapeFactory.dao("MGSCT_Valores_Produtos");
        NativeSqlDecorator nativeSqlDDecorator = new NativeSqlDecorator(this, "sql/BuscaNumeroUnicoPrecoServicoMaterialPrevisaoeAlocacao.sql");
        nativeSqlDDecorator.setParametro("NUMCONTRATO", numeroContrato);
        nativeSqlDDecorator.setParametro("CODTPN", codigoModalidade );
        nativeSqlDDecorator.setParametro("CODSERVMATERIAL", codigoServMaterial);
        nativeSqlDDecorator.setParametro("CODEVENTO", codigoEvento);

        BigDecimal numeroUnicoValoresProdutos = BigDecimal.ZERO;
        if (nativeSqlDDecorator.proximo()) {
            numeroUnicoValoresProdutos = nativeSqlDDecorator.getValorBigDecimal("NUCONTRMATSRV");
            if (numeroUnicoValoresProdutos == null) {
                numeroUnicoValoresProdutos = BigDecimal.ZERO;
            }
        }

        if (BigDecimal.ZERO.equals(numeroUnicoValoresProdutos)) {
            ErroUtils.disparaErro("Pre�o n�o localizado, favor verificar dados lancados!");
        }

        DynamicVO mgsct_valores_produtosVO = mgsct_valores_produtosDAO.findByPK(numeroUnicoValoresProdutos);
        if (mgsct_valores_produtosVO == null) {
            ErroUtils.disparaErro("Pre�o n�o localizado, favor verificar dados lancados!");
        }

        valorUnitario = mgsct_valores_produtosVO.asBigDecimal("VLRTOTAL").setScale(4,BigDecimal.ROUND_DOWN);
        return valorUnitario;
    }

    private void criaRegistrosDerivados() throws Exception {

    }

    public void validaDelete() throws Exception {
        if(vo.asBigDecimal("NUALOCASERV") != null){
            ErroUtils.disparaErro("Registro n�o pode excluido!");
        }
    }

    public void validaCamposUpdate(HashMap<String, Object[]> campos) throws Exception {
        String mensagemErro = "";

        //todo melhorar a descricao do campo pegando do dicionario de dados
        if (campos.containsKey("NUALOCASERV")) {mensagemErro += "Campo Nro. �nico n�o pode ser modificado. ";}
        if (campos.containsKey("NUMCONTRATO")) {mensagemErro += "Campo Num. Contrato n�o pode ser modificado. ";}
        if (campos.containsKey("NUUNIDPREV")) {mensagemErro += "Campo Unid. Previs�o n�o pode ser modificado. ";}
        if (campos.containsKey("QTDEALOCACAO")) {mensagemErro += "Campo Qtde Alocada n�o pode ser modificado. ";}
        if (campos.containsKey("VLRUNITARIO")) {mensagemErro += "Campo Valor Unit�rio n�o pode ser modificado. ";}
        if (campos.containsKey("VLRTOTAL")) {mensagemErro += "Campo Valor Total n�o pode ser modificado. ";}
        if (campos.containsKey("DTINS")) {mensagemErro += "Campo Dt. Inser��o n�o pode ser modificado. ";}

        BigDecimal qtdAlocacao = vo.asBigDecimal("QTDEALOCACAO");

        NativeSqlDecorator previsoesUnidadeServicoSQL = new NativeSqlDecorator("SELECT QTDCONTRATADA " +
                " FROM MGSVCTUNIDADEPREVSERV " +
                " WHERE NUUNIDPREV = :NUUNIDPREV AND ROWNUM <= 1");
        previsoesUnidadeServicoSQL.setParametro("NUUNIDPREV", vo.asBigDecimal("NUUNIDPREV"));

        if(previsoesUnidadeServicoSQL.proximo()){
            qtdContratada = previsoesUnidadeServicoSQL.getValorBigDecimal("QTDCONTRATADA");
        }

        if ( qtdAlocacao.compareTo(qtdContratada) > 0 ){
            mensagemErro += "Qtde Alocada n�o pode ser maior que a Qtde. Contratada !";
        }

        if (mensagemErro != "") {
            ErroUtils.disparaErro(mensagemErro);
        }
    }
}
