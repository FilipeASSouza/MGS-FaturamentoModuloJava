package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.mgs.ct.validator.PrevisaoValidator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Entidade: MGSCT_Previsoes_Contrato
 * Tabela: MGSTCTCONTRATOPREV
 * Chave: NUCONTRPREV
 */

public class PrevisoesContratoModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Previsoes_Contrato");
    private DynamicVO vo;

    /**
     * Entidade: MGSCT_Modalidade_Contrato
     * Tabela: MGSTCTMODALCONTR
     * Chave: NUMODALIDADE
     */
    private DynamicVO mestrevo;

    private String regraVadalicao = "";
    private BigDecimal codigoModelidade;
    private BigDecimal numeroContrato;
    private JdbcWrapper jdbcWrapper;
    private NativeSqlDecorator nativeSqlDDecorator;
    NativeSqlDecorator nativeSqlDDecorator2;
    VagasPrevisaoContratoModel vagasPrevisaoContratoModel;
    ApoioVagasModel modelApoio;
    public PrevisoesContratoModel(BigDecimal numeroUnico,JdbcWrapper jdbcWrapper) throws Exception {//Chave: NUCONTRPREV
        this.vo = dao.findByPK(numeroUnico);
        this.jdbcWrapper = jdbcWrapper;
        inicialzaVariaveis();
    }

    public PrevisoesContratoModel(DynamicVO dynamicVO,JdbcWrapper jdbcWrapper) throws Exception {
        this.vo = dynamicVO;
        this.jdbcWrapper = jdbcWrapper;
        inicialzaVariaveis();
    }

    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
        inicialzaVariaveis();
    }

    private void inicialzaVariaveis() throws Exception {
        mestrevo = JapeFactory.dao("MGSCT_Modalidade_Contrato").findByPK(vo.asBigDecimal("NUMODALIDADE"));
        codigoModelidade = mestrevo.asBigDecimal("CODTPN");
        numeroContrato = mestrevo.asBigDecimal("NUMCONTRATO");
        regraVadalicao = "";
        nativeSqlDDecorator = new NativeSqlDecorator(this, "sql/BuscaNumeroUnicoPrecoPostoPrevisaoeAlocacao.sql",jdbcWrapper);
        nativeSqlDDecorator2 = new NativeSqlDecorator(this, "sql/BuscaNumeroUnicoPrecoServicoMaterialPrevisaoeAlocacao.sql",jdbcWrapper);
        vagasPrevisaoContratoModel = new VagasPrevisaoContratoModel(vo,jdbcWrapper);
        modelApoio = new ApoioVagasModel(vo,jdbcWrapper);
    }

    public void validaDadosInsert() throws Exception {
        PrevisaoValidator previsaoValidator = new PrevisaoValidator();
        previsaoValidator.setVo(vo);
        previsaoValidator.validaDadosInsert();
        validaRegistroDuplicado();
    }

    public void validaRegistroDuplicado() throws Exception {
        DynamicVO registroJaCadastrados = dao.findOne("NUMODALIDADE  = ? AND NVL(CODTIPOPOSTO,0) = ? AND NVL(CODSERVMATERIAL,0) = ? AND  CODEVENTO = ? AND CODCONTROLE = ?",
                vo.asBigDecimal("NUMODALIDADE"),
                vo.asBigDecimalOrZero("CODTIPOPOSTO"),
                vo.asBigDecimalOrZero("CODSERVMATERIAL"),
                vo.asBigDecimal("CODEVENTO"),
                vo.asBigDecimal("CODCONTROLE"));

        if(registroJaCadastrados != null){
            ErroUtils.disparaErro("Registro ja cadastrado! Combinação posto, evento, controle ja existe cadastrado em numero unico "+registroJaCadastrados.asBigDecimal("NUMODALIDADE"));
        }
    }

    private void validaDadosUndate() throws Exception {

    }

    private String getRegraValidacao() throws Exception {
        if (regraVadalicao.equals("")) {
            DynamicVO eventoVO = JapeFactory.dao("TGFECUS").findByPK(vo.asBigDecimal("CODEVENTO"));
            String tipoEvento = eventoVO.asString("TIPOEVENTO");

            if ("S".equals(tipoEvento)) {
                BigDecimal codigoControle = vo.asBigDecimal("CODCONTROLE");
                if (codigoControle == null) {
                    ErroUtils.disparaErro("Controle deve ser preenchido para esse tipo de evento!");
                }
                regraVadalicao = tipoEvento + vo.asBigDecimal("CODCONTROLE").toString();
            } else {
                regraVadalicao = tipoEvento;
            }

            if (regraVadalicao == null) {
                regraVadalicao = "";
            }
        }

        return regraVadalicao;
    }

    public void preencheCamposCalculados() throws Exception {
        boolean postoPreechido = !(vo.asBigDecimalOrZero("CODTIPOPOSTO").equals(BigDecimal.ZERO));
        boolean servicoMaterialPreechido = !(vo.asBigDecimalOrZero("CODSERVMATERIAL").equals(BigDecimal.ZERO));

        BigDecimal valorUnitario = vo.asBigDecimalOrZero("VLRUNITARIO");
        BigDecimal quantidade = vo.asBigDecimalOrZero("QTDCONTRATADA");


        switch (getRegraValidacao()) {
            case "P"://posto
                valorUnitario = getPrecoPosto();
                if (BigDecimal.ZERO.equals(valorUnitario)) {
                    ErroUtils.disparaErro("Preço de posto localizado não pode ser zero, favor verificar dados lancados!");
                }
                break;
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
                valorUnitario = getPrecoServicoMaterial();
                if (BigDecimal.ZERO.equals(valorUnitario)) {
                    ErroUtils.disparaErro("Preço de Material/Serviço localizado não pode ser zero, favor verificar dados lancados!");
                }
                break;
            default:

        }

        vo.setProperty("VLRUNITARIO", valorUnitario);
        vo.setProperty("QTDCONTRATADA", quantidade);
        vo.setProperty("NUMCONTRATO", this.numeroContrato);
        vo.setProperty("VLRCONTRATADA", valorUnitario.multiply(quantidade));
    }

    private BigDecimal getPrecoPosto() throws Exception {
        BigDecimal valorUnitario;
        JapeWrapper mgsct_valores_eventosDAO = JapeFactory.dao("MGSCT_Valores_Eventos");
        
        nativeSqlDDecorator.cleanParameters();
        nativeSqlDDecorator.setParametro("NUMCONTRATO", this.numeroContrato);
        nativeSqlDDecorator.setParametro("CODTPN", this.codigoModelidade);
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

    private BigDecimal getPrecoServicoMaterial() throws Exception {
        BigDecimal valorUnitario;

        JapeWrapper mgsct_valores_produtosDAO = JapeFactory.dao("MGSCT_Valores_Produtos");
        
        nativeSqlDDecorator2.setParametro("NUMCONTRATO", this.numeroContrato);
        nativeSqlDDecorator2.setParametro("CODTPN", this.codigoModelidade);
        nativeSqlDDecorator2.setParametro("CODSERVMATERIAL", vo.asBigDecimal("CODSERVMATERIAL"));
        nativeSqlDDecorator2.setParametro("CODEVENTO", vo.asBigDecimal("CODEVENTO"));

        BigDecimal numeroUnicoValoresProdutos = BigDecimal.ZERO;
        if (nativeSqlDDecorator2.proximo()) {
            numeroUnicoValoresProdutos = nativeSqlDDecorator2.getValorBigDecimal("NUCONTRMATSRV");
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

    public void criaRegistrosDerivados() throws Exception {
        BigDecimal codigoTipoPosto = vo.asBigDecimalOrZero("CODTIPOPOSTO");
        boolean postoPreechido = !(codigoTipoPosto.equals(BigDecimal.ZERO));
        if (postoPreechido) {
            DynamicVO tgfpssVO = JapeFactory.dao("TGFPSS").findByPK(codigoTipoPosto);
            String prefixoposto = tgfpssVO.asString("PREFIXOPOSTO");
            ArrayList<DynamicVO> vagaVOs = criaVagas(prefixoposto);
            criaPrevisaoVagas(vagaVOs);
        }
    }

    private ArrayList<DynamicVO> criaVagas(String sigla) throws Exception {

        BigDecimal quantidadeContratada = vo.asBigDecimal("QTDCONTRATADA");
        BigDecimal numeroUnicoPreviesoContrato = vo.asBigDecimal("NUCONTRPREV");


        BigDecimal quantidadeVagasAtivas =vagasPrevisaoContratoModel.quantidadeVagasAtivas(numeroUnicoPreviesoContrato, sigla);

        if (quantidadeContratada.compareTo(quantidadeVagasAtivas) < 0) {
            ErroUtils.disparaErro("A quantidade de vagas não pode ser diminuida!");
        }

        BigDecimal quantidadeCriarNovasVagas = quantidadeContratada.subtract(quantidadeVagasAtivas);


        ArrayList<DynamicVO> dynamicVOS = modelApoio.criaVagas(quantidadeCriarNovasVagas, sigla);

        return dynamicVOS;

    }

    private void criaPrevisaoVagas(ArrayList<DynamicVO> vagaVOs) throws Exception {

        for (DynamicVO vagaVO : vagaVOs) {
            BigDecimal numeroUnicoPrevisoesContrato = vo.asBigDecimal("NUCONTRPREV");
            String codigoVaga = vagaVO.asString("CODVAGA");
            Timestamp dataInicio = mestrevo.asTimestamp("MGSCT_Dados_Contrato.DTINICIO");
            vagasPrevisaoContratoModel.criar(
                    numeroUnicoPrevisoesContrato,
                    codigoVaga,
                    dataInicio
            );
        }
    }

    public void diminuirUmQuantidadeContrata() throws Exception {
        FluidUpdateVO fluidUpdateVO = dao.prepareToUpdate(vo);
        fluidUpdateVO.set("QTDCONTRATADA", vo.asBigDecimal("QTDCONTRATADA").subtract(BigDecimal.ONE));
        fluidUpdateVO.update();
    }

    public void validaDelete() throws Exception {
        JapeWrapper previsoesUnidadeDAO = JapeFactory.dao("MGSCT_Previsoes_Unidade");
        Collection<DynamicVO> dynamicVOS = previsoesUnidadeDAO.find("NUMCONTRATO = ? AND CODEVENTO = ? AND NVL(CODTIPOPOSTO,0) = ? AND NVL(CODSERVMATERIAL,0) = ?",
                vo.asBigDecimal("NUMCONTRATO"),
                vo.asBigDecimal("CODEVENTO"),
                vo.asBigDecimalOrZero("CODTIPOPOSTO"),
                vo.asBigDecimalOrZero("CODSERVMATERIAL")
        );
        if (dynamicVOS.size() > 0) {
            ErroUtils.disparaErro("Previsão do Contrato já possui Previsão na Unidade e não pode ser deletado!");
        }

    }

    public void validaCamposUpdate(HashMap<String, Object[]> campos) throws Exception {
        String mensagemErro = "";
        if (vo.asBigDecimalOrZero("CODCONTROLE").equals(new BigDecimal(3)) || vo.asBigDecimalOrZero("CODCONTROLE").equals(new BigDecimal(4)))
            if (campos.containsKey("VLRUNITARIO")) {
                mensagemErro += "Campo Vlr. Unitário não pode ser modificado. ";
            }

        if (campos.containsKey("CODEVENTO")) {
            mensagemErro += "Campo Evento não pode ser modificado. ";
        }

        if (campos.containsKey("CODSERVMATERIAL")) {
            mensagemErro += "Campo Serviço ou Material não pode ser modificado. ";
        }

        if (campos.containsKey("CODCONTROLE")) {
            mensagemErro += "Campo Controle não pode ser modificado. ";
        }

        if (campos.containsKey("CODTIPOPOSTO")) {
            mensagemErro += "Campo Tipo do Posto não pode ser modificado. ";
        }

        if (mensagemErro != "") {
            ErroUtils.disparaErro(mensagemErro);
        }

    }

    public void recalculaCamposCalculados() {
        BigDecimal valorUnitario = vo.asBigDecimalOrZero("VLRUNITARIO");
        BigDecimal quantidade = vo.asBigDecimalOrZero("QTDCONTRATADA");
        vo.setProperty("VLRCONTRATADA", valorUnitario.multiply(quantidade));
    }
}
