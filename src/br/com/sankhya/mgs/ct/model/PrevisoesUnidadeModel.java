package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.mgs.ct.validator.PrevisaoValidator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Entidade: MGSCT_Previsoes_Unidade
 * Tabela: MGSTCTUNIDADEPREV
 * Chave: NUUNIDPREV
 */
public class PrevisoesUnidadeModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Previsoes_Unidade");;
    private DynamicVO vo;

    /**
     * Entidade: MGSCT_Unidades
     * Tabela: MGSTCTCONTRCENT
     * Chave: NUCONTRCENT
     */
    private DynamicVO mestrevo;

    /**
     * Entidade: MGSCT_Previsoes_Contrato
     * Tabela: MGSTCTCONTRATOPREV
     * Chave: NUCONTRPREV
     */
    private DynamicVO previsoesContratoVO;
    private BigDecimal numeroContrato;
    private BigDecimal numeroUnicoModalidade;
    private PrevisaoValidator previsaoValidator;
    public PrevisoesUnidadeModel()  {

    }

    PrevisoesUnidadeModel(BigDecimal numeroUnico) throws Exception {//Chave: NUUNIDPREV
        this.vo = dao.findByPK(numeroUnico);
        inicialzaVariaveis();
    }

    private PrevisoesUnidadeModel(DynamicVO dynamicVO) throws Exception {
        this.vo = dynamicVO;
        inicialzaVariaveis();
    }

    public void diminuirUmQuantidadeContrata() throws Exception {
        FluidUpdateVO fluidUpdateVO = dao.prepareToUpdate(vo);
        fluidUpdateVO.set("QTDCONTRATADA", vo.asBigDecimal("QTDCONTRATADA").subtract(BigDecimal.ONE));
        fluidUpdateVO.update();
    }


    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
        inicialzaVariaveis();
    }

    private void inicialzaVariaveis()throws Exception {
        previsaoValidator = new PrevisaoValidator();
        previsaoValidator.setVo(vo);

        mestrevo = JapeFactory.dao("MGSCT_Unidades").findByPK(vo.asBigDecimal("NUCONTRCENT"));
        numeroContrato = mestrevo.asBigDecimal("NUMCONTRATO");
        numeroUnicoModalidade = mestrevo.asBigDecimal("MGSCT_Local_Contrato.NUMODALIDADE");
        JapeWrapper previsoesContratoDAO = JapeFactory.dao("MGSCT_Previsoes_Contrato");
        previsoesContratoVO = previsoesContratoDAO.findOne("NVL(NUMCONTRATO,0) = ? AND NVL(CODTIPOPOSTO,0) = ? AND NVL(CODSERVMATERIAL,0) = ? AND NVL(CODEVENTO,0) = ?  AND NVL(NUMODALIDADE,0) =?",
                numeroContrato,
                vo.asBigDecimalOrZero("CODTIPOPOSTO"),
                vo.asBigDecimalOrZero("CODSERVMATERIAL"),
                vo.asBigDecimalOrZero("CODEVENTO"),
                numeroUnicoModalidade
        );
        //, NUMCONTRATO, CODEVENTO, CODTIPOPOSTO, CODSERVMATERIAL

    }

    public void validaDadosInsert() throws Exception {
        previsaoValidator.validaDadosInsert();

        if (previsoesContratoVO==null){
            ErroUtils.disparaErro("Não foi encontrado uma provisão do contrado com os mesmos dados da previsão unidade lancada!");
        }
        //todo valida quantidade com quantidade do contrato e vagas previstas na unidade
    }

    public void validaDadosUpdate() throws Exception {

    }

    public void preecheCamposCalculados() throws Exception {

        boolean postoPreechido = !(vo.asBigDecimalOrZero("CODTIPOPOSTO").equals(BigDecimal.ZERO));
        boolean servicoMaterialPreechido = !(vo.asBigDecimalOrZero("CODSERVMATERIAL").equals(BigDecimal.ZERO));

        BigDecimal valorUnitario = vo.asBigDecimalOrZero("VLRUNITARIO");
        BigDecimal quantidade = vo.asBigDecimalOrZero("QTDCONTRATADA");


        switch (previsaoValidator.getRegraValidacao()) {
            case "P"://posto
                if (previsoesContratoVO == null){
                    ErroUtils.disparaErro("Previsão de contrato não localizada");
                }
                valorUnitario = previsoesContratoVO.asBigDecimal("VLRUNITARIO");

                if (BigDecimal.ZERO.equals(valorUnitario)) {
                    ErroUtils.disparaErro("Preço de posto localizado não pode ser zero, favor verificar a prevsão do contrato!");
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
                if (previsoesContratoVO == null){
                    ErroUtils.disparaErro("Previsão de contrato não localizada");
                }
                valorUnitario = previsoesContratoVO.asBigDecimal("VLRUNITARIO");
                if (BigDecimal.ZERO.equals(valorUnitario)) {
                    ErroUtils.disparaErro("Preço de Material/Serviço localizado não pode ser zero, favor verificar previsão do contrato!");
                }
                break;
            default:

        }

        vo.setProperty("VLRUNITARIO", valorUnitario);
        vo.setProperty("QTDCONTRATADA", quantidade);
        vo.setProperty("NUMCONTRATO", this.numeroContrato);
        vo.setProperty("VLRCONTRATADA", valorUnitario.multiply(quantidade));
        if (!BigDecimal.ZERO.equals(previsoesContratoVO.asBigDecimalOrZero("CODCONTROLE"))){
            vo.setProperty("CODCONTROLE", previsoesContratoVO.asBigDecimal("CODCONTROLE"));
        }


    }
    public void criaRegistrosDerivados() throws Exception {
        BigDecimal codigoTipoPosto = vo.asBigDecimalOrZero("CODTIPOPOSTO");
        boolean postoPreechido = !(codigoTipoPosto.equals(BigDecimal.ZERO));
        if (postoPreechido) {


            ArrayList<DynamicVO> vagaLivresVOs = new VagasPrevisaoContratoModel().getVagasLivres(previsoesContratoVO.asBigDecimalOrZero("NUCONTRPREV") );

            int quantidadeContratadaInt = new Integer(vo.asBigDecimalOrZero("QTDCONTRATADA").toString()).intValue()  ;
            BigDecimal quantidadeContratada = vo.asBigDecimalOrZero("QTDCONTRATADA") ;



            BigDecimal numeroUnicoPrevisaoUnidade = vo.asBigDecimal("NUUNIDPREV");
            BigDecimal quantidadeVagasAtribuidasAtivas = new VagasPrevisaoUnidadeModel().quantidadeVagasAtivas(numeroUnicoPrevisaoUnidade);

            if (quantidadeContratada.compareTo(quantidadeVagasAtribuidasAtivas) < 0) {
                ErroUtils.disparaErro("A quantidade de vagas não pode ser diminuida!");
            }

            BigDecimal quantidadeCriarNovasVagas = quantidadeContratada.subtract(quantidadeVagasAtribuidasAtivas);

            if (new BigDecimal(vagaLivresVOs.size()).compareTo(quantidadeCriarNovasVagas) <0){
                ErroUtils.disparaErro("Quantidade de vagas livres menor que a solicitada na previsa da unidade");
            }

            ArrayList<DynamicVO> vagaVOs = new ArrayList();

            for (BigDecimal i = BigDecimal.ZERO; i.compareTo(quantidadeCriarNovasVagas)<0; i = i.add(BigDecimal.ONE)){
                vagaVOs.add(vagaLivresVOs.remove(0));
            }

            criaPrevisaoVagas(vagaVOs);
        }
    }

    private void criaPrevisaoVagas(ArrayList<DynamicVO> vagaVOs) throws Exception {
        VagasPrevisaoUnidadeModel vagasPrevisaoUnidadeModel = new VagasPrevisaoUnidadeModel();
        for (DynamicVO vagaVO : vagaVOs) {
            BigDecimal numeroUnicoPrevisaoUnidade = vo.asBigDecimal("NUUNIDPREV");
            String codigoVaga = vagaVO.asString("CODVAGA");
            Timestamp dataInicioUnidade = mestrevo.asTimestamp("DTINICIO");
            vagasPrevisaoUnidadeModel.criar(
                    numeroUnicoPrevisaoUnidade,
                    codigoVaga,
                    dataInicioUnidade
                    );
        }
    }
}
