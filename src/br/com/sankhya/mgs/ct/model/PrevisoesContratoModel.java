package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Entidade: MGSCT_Previsoes_Contrato
 * Tabela: MGSTCTCONTRATOPREV
 * Chave: NUCONTRPREV
 */
public class PrevisoesContratoModel {
    private JapeWrapper dao;
    private DynamicVO vo;

    private PrevisoesContratoModel() {
        inicialzaVariaveis();
    }


    public PrevisoesContratoModel(DynamicVO dynamicVO) {
        inicialzaVariaveis();
        this.vo = dynamicVO;
    }

    private void inicialzaVariaveis() {
        dao = JapeFactory.dao("MGSCT_Previsoes_Contrato");
    }

    public PrevisoesContratoModel validaDados() throws Exception {
        boolean postoPreechido = !(vo.asBigDecimalOrZero("CODTIPOPOSTO").equals(BigDecimal.ZERO));
        boolean servicoMaterialPreechido = !(vo.asBigDecimalOrZero("CODSERVMATERIAL").equals(BigDecimal.ZERO));

        if (postoPreechido && servicoMaterialPreechido) {
            ErroUtils.disparaErro("Campos Tipos do Posto e Serviço/Material não podem ser preechidos no mesmo lançamento!");
        }
        return this;
    }

    public void preecheCamposCalculados() throws Exception {
        boolean postoPreechido = !(vo.asBigDecimalOrZero("CODTIPOPOSTO").equals(BigDecimal.ZERO));
        boolean servicoMaterialPreechido = !(vo.asBigDecimalOrZero("CODSERVMATERIAL").equals(BigDecimal.ZERO));
        BigDecimal valorUnitario = BigDecimal.ZERO;
        if (servicoMaterialPreechido) {
            JapeWrapper mgsct_valores_produtosDAO = JapeFactory.dao("MGSCT_Valores_Produtos");
            NativeSqlDecorator nativeSqlDDecorator = new NativeSqlDecorator(this, "sql/BuscaNumeroUnicoPrecoServicoMaterial.sql");
            nativeSqlDDecorator.setParametro("NUMCONTRATO",vo.asBigDecimal("NUMCONTRATO"));
            nativeSqlDDecorator.setParametro("NUMODALIDADE",vo.asBigDecimal("NUMODALIDADE"));
            nativeSqlDDecorator.setParametro("CODSERVMATERIAL",vo.asBigDecimal("CODSERVMATERIAL"));
            nativeSqlDDecorator.setParametro("CODEVENTO",vo.asBigDecimal("CODEVENTO"));
            nativeSqlDDecorator.proximo();
            BigDecimal numeroUnicoValoresProdutos = nativeSqlDDecorator.getValorBigDecimal("NUCONTRMATSRV");
            if (numeroUnicoValoresProdutos == null){
                ErroUtils.disparaErro("Preço não localizado, favor verificar dados lancados!");
            }

            DynamicVO mgsct_valores_produtosVO = mgsct_valores_produtosDAO.findByPK(numeroUnicoValoresProdutos);
            if (mgsct_valores_produtosVO == null){
                ErroUtils.disparaErro("Preço não localizado, favor verificar dados lancados!");
            }

            valorUnitario = mgsct_valores_produtosVO.asBigDecimal("VLRTOTAL");
        } else if (postoPreechido){

            JapeWrapper mgsct_valores_eventosDAO = JapeFactory.dao("MGSCT_Valores_Eventos");
            NativeSqlDecorator nativeSqlDDecorator = new NativeSqlDecorator(this, "sql/BuscaNumeroUnicoPrecoPosto.sql");
            nativeSqlDDecorator.setParametro("NUMCONTRATO",vo.asBigDecimal("NUMCONTRATO"));
            nativeSqlDDecorator.setParametro("NUMODALIDADE",vo.asBigDecimal("NUMODALIDADE"));
            nativeSqlDDecorator.setParametro("CODTIPOPOSTO",vo.asBigDecimal("CODTIPOPOSTO"));
            nativeSqlDDecorator.setParametro("CODEVENTO",vo.asBigDecimal("CODEVENTO"));
            nativeSqlDDecorator.proximo();
            BigDecimal numeroUnicoValoresEventos = nativeSqlDDecorator.getValorBigDecimal("NUCONTREVENTO");
            if (numeroUnicoValoresEventos == null){
                ErroUtils.disparaErro("Preço não localizado, favor verificar dados lancados!");
            }

            DynamicVO mgsct_valores_eventosVO = mgsct_valores_eventosDAO.findByPK(numeroUnicoValoresEventos);
            if (mgsct_valores_eventosVO == null){
                ErroUtils.disparaErro("Preço não localizado, favor verificar dados lancados!");
            }

            valorUnitario = mgsct_valores_eventosVO.asBigDecimal("VLRTOTAL");



        }

        if (BigDecimal.ZERO.equals(valorUnitario)){
            ErroUtils.disparaErro("Preço localizado não pode ser zero, favor verificar dados lancados!");
        }

        FluidUpdateVO fluidUpdateVO = dao.prepareToUpdate(vo);
        fluidUpdateVO.set("VLRUNITARIO",valorUnitario);
        fluidUpdateVO.set("VLRCONTRATADA",valorUnitario.multiply(vo.asBigDecimal("QTDCONTRATADA")));
        fluidUpdateVO.update();



    }

    public void criaRegistrosDerivados() throws Exception {
        BigDecimal codigoTipoPosto = vo.asBigDecimalOrZero("CODTIPOPOSTO");
        boolean postoPreechido = !(codigoTipoPosto.equals(BigDecimal.ZERO));
        if (postoPreechido){
            DynamicVO tgfpssVO = JapeFactory.dao("TGFPSS").findByPK(codigoTipoPosto);
            String prefixoposto = tgfpssVO.asString("PREFIXOPOSTO");
            ArrayList<DynamicVO> vagasVO = criaVagas(prefixoposto);

        }
    }

    private ArrayList<DynamicVO> criaVagas(String sigla ) throws Exception {

        BigDecimal quantidadeContratada = vo.asBigDecimal("QTDCONTRATADA");

        ArrayList<DynamicVO> dynamicVOS = new ApoioVagasModel().criaVagas(quantidadeContratada, sigla);
        return dynamicVOS;

    }

    private void criaPrevisaoVagas(ArrayList<DynamicVO> vagasVO) throws Exception {
        VagasPrevisaoContratoModel vagasPrevisaoContratoModel = new VagasPrevisaoContratoModel();
        for(DynamicVO vagaVO:vagasVO) {
            vagasPrevisaoContratoModel.criar(vo.asBigDecimal("NUCONTRPREV"), vagaVO.asBigDecimal("CODVAGA"));
        }
    }
}
