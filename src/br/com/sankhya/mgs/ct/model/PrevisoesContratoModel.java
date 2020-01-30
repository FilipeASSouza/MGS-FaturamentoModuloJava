package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.mgs.ct.validator.PrevisaoValidator;

import java.math.BigDecimal;
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
    private String regraVadalicao = "";
    private BigDecimal codigoModelidade;
    private BigDecimal numeroContrato;

    public PrevisoesContratoModel() {
    }

    public PrevisoesContratoModel(BigDecimal numeroUnico) throws Exception {//Chave: NUCONTRPREV
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    public PrevisoesContratoModel(DynamicVO dynamicVO) throws Exception {
        this.vo = dynamicVO;
        inicialzaVariaveis();
    }

    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
        inicialzaVariaveis();
    }

    private void inicialzaVariaveis() throws Exception {
        DynamicVO modalidadeContratoVO = JapeFactory.dao("MGSCT_Modalidade_Contrato").findByPK(vo.asBigDecimal("NUMODALIDADE"));
        codigoModelidade = modalidadeContratoVO.asBigDecimal("CODTPN");
        numeroContrato = modalidadeContratoVO.asBigDecimal("NUMCONTRATO");
        regraVadalicao = "";
    }

    public void validaDadosInsert() throws Exception {
        PrevisaoValidator previsaoValidator = new PrevisaoValidator();
        previsaoValidator.setVo(vo);
        previsaoValidator.validaDadosInsert();
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

    public void preecheCamposCalculados() throws Exception {
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
                quantidade = BigDecimal.ONE;
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
        NativeSqlDecorator nativeSqlDDecorator = new NativeSqlDecorator(this, "sql/BuscaNumeroUnicoPrecoPosto.sql");
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
        NativeSqlDecorator nativeSqlDDecorator = new NativeSqlDecorator(this, "sql/BuscaNumeroUnicoPrecoServicoMaterial.sql");
        nativeSqlDDecorator.setParametro("NUMCONTRATO", this.numeroContrato);
        nativeSqlDDecorator.setParametro("CODTPN", this.codigoModelidade);
        nativeSqlDDecorator.setParametro("CODSERVMATERIAL", vo.asBigDecimal("CODSERVMATERIAL"));
        nativeSqlDDecorator.setParametro("CODEVENTO", vo.asBigDecimal("CODEVENTO"));

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


        BigDecimal quantidadeVagasAtivas = new VagasPrevisaoContratoModel().quantidadeVagasAtivas(numeroUnicoPreviesoContrato, sigla);

        if (quantidadeContratada.compareTo(quantidadeVagasAtivas) < 0) {
            ErroUtils.disparaErro("A quantidade de vagas não pode ser diminuida!");
        }

        BigDecimal quantidadeCriarNovasVagas = quantidadeContratada.subtract(quantidadeVagasAtivas);


        ArrayList<DynamicVO> dynamicVOS = new ApoioVagasModel().criaVagas(quantidadeCriarNovasVagas, sigla);

        return dynamicVOS;

    }

    private void criaPrevisaoVagas(ArrayList<DynamicVO> vagaVOs) throws Exception {
        VagasPrevisaoContratoModel vagasPrevisaoContratoModel = new VagasPrevisaoContratoModel();
        for (DynamicVO vagaVO : vagaVOs) {
            vagasPrevisaoContratoModel.criar(vo.asBigDecimal("NUCONTRPREV"), vagaVO.asString("CODVAGA"));
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

    public void validaUpdate(HashMap<String, Object[]> campos) throws Exception {
        if (campos.size() > 0) {
            String primeiroCampo = (String) campos.keySet().toArray()[0];
            switch (primeiroCampo) {
                case "VLRUNITARIO":
                    ErroUtils.disparaErro("Campo Vlr. Unitário não pode ser modificado");
                    break;
                case "CODEVENTO":
                    ErroUtils.disparaErro("Campo Evento não pode ser modificado");
                    break;
                case "CODSERVMATERIAL":
                    ErroUtils.disparaErro("Campo Serviço ou Material não pode ser modificado");
                    break;
                case "CODCONTROLE":
                    ErroUtils.disparaErro("Campo Controle não pode ser modificado");
                    break;
                case "CODTIPOPOSTO":
                    ErroUtils.disparaErro("Campo Tipo do Posto não pode ser modificado");
                    break;
            }
        }
    }
}
