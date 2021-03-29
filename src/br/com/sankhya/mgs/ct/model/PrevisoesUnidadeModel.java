package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.bh.utils.NativeSqlDecorator;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.mgs.ct.validator.PrevisaoValidator;
import org.apache.poi.ss.formula.functions.Na;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * Entidade: MGSCT_Previsoes_Unidade
 * Tabela: MGSTCTUNIDADEPREV
 * Chave: NUUNIDPREV
 */
public class PrevisoesUnidadeModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Previsoes_Unidade");
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
    private static Map<BigDecimal, Timestamp> listaDataIncioVaga = new HashMap<BigDecimal,Timestamp>();
    private static Map<BigDecimal, String> listaVagasRemanajedas = new HashMap<BigDecimal,String>();

    public PrevisoesUnidadeModel() {

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

    private void inicialzaVariaveis() throws Exception {
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

        if (listaDataIncioVaga.containsKey(BigDecimal.ZERO)){
            listaDataIncioVaga.get(BigDecimal.ZERO);
            listaDataIncioVaga.put(vo.asBigDecimal("NUUNIDPREV"),listaDataIncioVaga.get(BigDecimal.ZERO));
            listaDataIncioVaga.remove(listaDataIncioVaga.get(BigDecimal.ZERO));
        }

    }

    private Collection<DynamicVO> buscaPrevisoesUnidadeDeMesmoPrevisaoContrato() throws Exception {
        Collection<DynamicVO> dynamicVOS = dao.find("NVL(NUMCONTRATO,0) = ? AND NVL(CODTIPOPOSTO,0) = ? AND NVL(CODSERVMATERIAL,0) = ? AND NVL(CODEVENTO,0) = ? AND NUCONTRCENT IN (SELECT NUCONTRCENT FROM MGSTCTCONTRCENT WHERE NULOCALCONT IN (SELECT NULOCALCONT FROM MGSTCTLOCALCONT WHERE NUMODALIDADE = ?))",
                numeroContrato,
                vo.asBigDecimalOrZero("CODTIPOPOSTO"),
                vo.asBigDecimalOrZero("CODSERVMATERIAL"),
                vo.asBigDecimalOrZero("CODEVENTO"),
                numeroUnicoModalidade
        );
        return dynamicVOS;
    }

    private BigDecimal consultaQuantidadePrevisaoUnidade() throws Exception{

        BigDecimal contrato = mestrevo.asBigDecimal("NUMCONTRATO");
        BigDecimal codTipoPosto = vo.asBigDecimalOrZero("CODTIPOPOSTO");
        BigDecimal codServicoMaterial = vo.asBigDecimalOrZero("CODSERVMATERIAL");
        BigDecimal codEvento = vo.asBigDecimalOrZero("CODEVENTO");
        Timestamp dataInicio = vo.asTimestamp("DTINICIO");

        NativeSqlDecorator consultaQuantidadePrevisaoUnidade = new NativeSqlDecorator(" SELECT " +
                " sum(qtdcontratada) QTDCONTRATADA " +
                " FROM MGSTCTUNIDADEPREV " +
                " WHERE " +
                " NVL(NUMCONTRATO,0) = :NUMCONTRATO AND NVL(CODTIPOPOSTO,0) = :CODTIPOPOSTO AND NVL(CODSERVMATERIAL,0) = :CODSERVMATERIAL " +
                " AND NVL( CODEVENTO ,0 ) = :CODEVENTO " +
                " AND NVL(DTFIM, SYSDATE ) > :DT ");

        consultaQuantidadePrevisaoUnidade.setParametro("NUMCONTRATO", contrato);
        consultaQuantidadePrevisaoUnidade.setParametro("CODTIPOPOSTO", codTipoPosto);
        consultaQuantidadePrevisaoUnidade.setParametro("CODSERVMATERIAL", codServicoMaterial);
        consultaQuantidadePrevisaoUnidade.setParametro("CODEVENTO", codEvento);
        consultaQuantidadePrevisaoUnidade.setParametro("DT", dataInicio == null ? mestrevo.asTimestamp("DTINICIO") : dataInicio );

        BigDecimal qtdTotalPrevisaoUnidade = null;

        if(consultaQuantidadePrevisaoUnidade.proximo()){
            qtdTotalPrevisaoUnidade = consultaQuantidadePrevisaoUnidade.getValorBigDecimal("QTDCONTRATADA");
        }

        return qtdTotalPrevisaoUnidade;
    }

    public void validaDadosInsert() throws Exception {
        if (!BigDecimal.ZERO.equals(previsoesContratoVO.asBigDecimalOrZero("CODCONTROLE"))) {
            vo.setProperty("CODCONTROLE", previsoesContratoVO.asBigDecimal("CODCONTROLE"));
        }

        if( ( vo.asTimestamp("DTFIM") != null && vo.asTimestamp("DTINICIO") != null )
                && vo.asTimestamp("DTFIM").compareTo(vo.asTimestamp("DTINICIO")) < 0 ){
            ErroUtils.disparaErro("Data Final deve ser maior que a data Inicial! Fineza verificar!");
        }

        previsaoValidator.validaDadosInsert();

        validaRegistroDuplicado();

        if (previsoesContratoVO == null) {
            ErroUtils.disparaErro("Não foi encontrado uma provisão do contrado com os mesmos dados da previsão unidade lancada!");
        }

        switch (previsaoValidator.getRegraValidacao()) {
            case "P"://posto
            case "S3"://serviceo/material controle 3
            case "S4"://serviceo/material controle 4
                //todo valida quantidade total da unidade com contrato
                if (!validaQuantidadeTotalUnidadesPeloContrato()) {
                    ErroUtils.disparaErro("Quantidade total das unidades ultrapassou o permitido no contrato!");
                }
                break;

            case "C"://contrato
            case "S1"://servico/material controle 1
            case "S2"://servico/material controle 2
                if (vo.asBigDecimalOrZero("QTDCONTRATADA").equals(BigDecimal.ZERO)){
                    vo.setProperty("QTDCONTRATADA",BigDecimal.ONE);
                }

                //todo valida valor total da unidade com contrato
                if (!validaValorTotalUnidadesPeloContrato()) {
                    ErroUtils.disparaErro("Valor total das unidades ultrapassou o permitido no contrato!");
                }
                break;
            case "R"://rescisao
            default:
        }
    }

    public void validaRegistroDuplicado() throws Exception {
        DynamicVO registroJaCadastrados = dao.findOne("NUCONTRCENT = ? AND NVL(CODTIPOPOSTO,0) = ? AND NVL(CODSERVMATERIAL,0) = ?  AND CODEVENTO = ? AND CODCONTROLE = ?",
                vo.asBigDecimal("NUCONTRCENT"),
                vo.asBigDecimalOrZero("CODTIPOPOSTO"),
                vo.asBigDecimalOrZero("CODSERVMATERIAL"),
                vo.asBigDecimal("CODEVENTO"),
                vo.asBigDecimal("CODCONTROLE"));

        if(registroJaCadastrados != null){
            ErroUtils.disparaErro("Registro ja cadastrado! Combinação posto, evento, controle ja existe cadastrado !");
        }
    }

    private boolean validaQuantidadeTotalUnidadesPeloContrato() throws Exception {

        BigDecimal quantidadeContratadaUnidadesTotal = consultaQuantidadePrevisaoUnidade().add(vo.asBigDecimalOrZero("QTDCONTRATADA"));
        BigDecimal quantidadePrevisaoContrato = previsoesContratoVO.asBigDecimalOrZero("QTDCONTRATADA");

        Boolean validado = quantidadeContratadaUnidadesTotal.compareTo(quantidadePrevisaoContrato) <= 0;

        return validado;
    }

    private boolean validaValorTotalUnidadesPeloContrato() throws Exception {
        Collection<DynamicVO> previsaoUndadeVOS = buscaPrevisoesUnidadeDeMesmoPrevisaoContrato();
        BigDecimal valorContratadaOutrasUnidades = BigDecimal.ZERO;
        for (DynamicVO previsaoUnidadeVO : previsaoUndadeVOS) {
            BigDecimal qtdcontratada = previsaoUnidadeVO.asBigDecimalOrZero("QTDCONTRATADA").multiply(previsaoUnidadeVO.asBigDecimalOrZero("VLRUNITARIO"));
            valorContratadaOutrasUnidades = valorContratadaOutrasUnidades.add(qtdcontratada);
        }

        BigDecimal valorContratadaUnidadesTotal = valorContratadaOutrasUnidades.add(vo.asBigDecimalOrZero("QTDCONTRATADA").multiply(vo.asBigDecimalOrZero("VLRUNITARIO")));
        BigDecimal valorPrevisaoContrato = previsoesContratoVO.asBigDecimalOrZero("QTDCONTRATADA").multiply(previsoesContratoVO.asBigDecimalOrZero("VLRUNITARIO"));

        Boolean validado = valorContratadaUnidadesTotal.compareTo(valorPrevisaoContrato) <= 0;

        return validado;
    }


    public void preencheCamposCalculados() throws Exception {

        BigDecimal valorUnitario = vo.asBigDecimalOrZero("VLRUNITARIO");
        BigDecimal quantidade = vo.asBigDecimalOrZero("QTDCONTRATADA");


        switch (previsaoValidator.getRegraValidacao()) {
            case "P"://posto
                if (previsoesContratoVO == null) {
                    ErroUtils.disparaErro("Previsão de contrato não localizada");
                }
                valorUnitario = previsoesContratoVO.asBigDecimal("VLRUNITARIO");

                if (BigDecimal.ZERO.equals(valorUnitario)) {
                    ErroUtils.disparaErro("Preço de posto localizado não pode ser zero, favor verificar a prevsão do contrato!");
                }
                break;
            case "C"://contrato
            case "R"://rescisao
            case "S1"://servico/material controle 1
            case "S2"://servico/material controle 2
                if (quantidade.equals(BigDecimal.ZERO)) {
                    quantidade = BigDecimal.ONE;
                }
                break;
            case "S3"://serviceo/material controle 3
            case "S4"://serviceo/material controle 4
                if (previsoesContratoVO == null) {
                    ErroUtils.disparaErro("Previsão de contrato não localizada");
                }
                valorUnitario = previsoesContratoVO.asBigDecimal("VLRUNITARIO");
                if (BigDecimal.ZERO.equals(valorUnitario)
                    || valorUnitario == null) {
                    ErroUtils.disparaErro("Preço de Material/Serviço localizado não pode ser zero, favor verificar previsão do contrato!");
                }
                break;
            default:

        }

        vo.setProperty("VLRUNITARIO", valorUnitario);
        vo.setProperty("QTDCONTRATADA", quantidade);
        vo.setProperty("NUMCONTRATO", this.numeroContrato);
        vo.setProperty("VLRCONTRATADA", valorUnitario.multiply(quantidade));

        if (!BigDecimal.ZERO.equals(previsoesContratoVO.asBigDecimalOrZero("CODCONTROLE"))) {
            vo.setProperty("CODCONTROLE", previsoesContratoVO.asBigDecimal("CODCONTROLE"));
        }
    }

    public void criaRegistrosDerivados() throws Exception {
        BigDecimal codigoTipoPosto = vo.asBigDecimalOrZero("CODTIPOPOSTO");
        boolean postoPreechido = !(codigoTipoPosto.equals(BigDecimal.ZERO));
        BigDecimal numeroUnicoPrevisaoUnidade = vo.asBigDecimal("NUUNIDPREV");
        if (postoPreechido) {
            ArrayList<DynamicVO> vagaVOs = new ArrayList();
            if (listaVagasRemanajedas.containsKey(numeroUnicoPrevisaoUnidade)){
                String codigoVaga = listaVagasRemanajedas.get(numeroUnicoPrevisaoUnidade);
                listaVagasRemanajedas.remove(numeroUnicoPrevisaoUnidade);
                vagaVOs.add((DynamicVO) JapeFactory.dao("MGSCT_Vagas_Previsao_Contrato").findOne("CODVAGA = ? ", codigoVaga));

            }else {
                ArrayList<DynamicVO> vagaLivresVOs = new VagasPrevisaoContratoModel().getVagasLivres(previsoesContratoVO.asBigDecimalOrZero("NUCONTRPREV"));

                int quantidadeContratadaInt = new Integer(vo.asBigDecimalOrZero("QTDCONTRATADA").toString()).intValue();
                BigDecimal quantidadeContratada = vo.asBigDecimalOrZero("QTDCONTRATADA");

                BigDecimal quantidadeVagasAtribuidasAtivas = new VagasPrevisaoUnidadeModel().quantidadeVagasAtivas(numeroUnicoPrevisaoUnidade);

                if (quantidadeContratada.compareTo(quantidadeVagasAtribuidasAtivas) < 0) {
                    ErroUtils.disparaErro("A quantidade de vagas não pode ser diminuida!");
                }

                BigDecimal quantidadeCriarNovasVagas = quantidadeContratada.subtract(quantidadeVagasAtribuidasAtivas);

                if (new BigDecimal(vagaLivresVOs.size()).compareTo(quantidadeCriarNovasVagas) < 0) {
                    ErroUtils.disparaErro("Quantidade de vagas livres menor que a solicitada na previsa da unidade");
                }

                for (BigDecimal i = BigDecimal.ZERO; i.compareTo(quantidadeCriarNovasVagas) < 0; i = i.add(BigDecimal.ONE)) {
                    vagaVOs.add(vagaLivresVOs.remove(0));
                }
            }
            criaPrevisaoVagas(vagaVOs);
        }
    }

    private void criaPrevisaoVagas(ArrayList<DynamicVO> vagaVOs) throws Exception {
        VagasPrevisaoUnidadeModel vagasPrevisaoUnidadeModel = new VagasPrevisaoUnidadeModel();
        for (DynamicVO vagaVO : vagaVOs) {
            BigDecimal numeroUnicoPrevisaoUnidade = vo.asBigDecimal("NUUNIDPREV");
            String codigoVaga = vagaVO.asString("CODVAGA");
            Timestamp dataInicioUnidade = null;
            if (listaDataIncioVaga.containsKey(numeroUnicoPrevisaoUnidade)){
                dataInicioUnidade = listaDataIncioVaga.get(numeroUnicoPrevisaoUnidade);
                listaDataIncioVaga.remove(numeroUnicoPrevisaoUnidade);
            } else {
                dataInicioUnidade = mestrevo.asTimestamp("DTINICIO");
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(dataInicioUnidade);
            cal.add(Calendar.DATE, 1);
            Timestamp dataDiaSeguinte = new Timestamp(cal.getTime().getTime());

            vagasPrevisaoUnidadeModel.criar(
                    numeroUnicoPrevisaoUnidade,
                    codigoVaga,
                    dataDiaSeguinte
            );
        }
    }

    public void recalculaCamposCalculados() {
        BigDecimal valorUnitario = vo.asBigDecimalOrZero("VLRUNITARIO");
        BigDecimal quantidade = vo.asBigDecimalOrZero("QTDCONTRATADA");
        vo.setProperty("VLRCONTRATADA", valorUnitario.multiply(quantidade));
    }

    public void validaCamposUpdate(HashMap<String, Object[]> campos) throws Exception {
        String mensagemErro = "";
        if (vo.asBigDecimalOrZero("CODCONTROLE").equals(new BigDecimal(3)) || vo.asBigDecimalOrZero("CODCONTROLE").equals(new BigDecimal(4)))
            if (campos.containsKey("VLRUNITARIO")) {
                mensagemErro += "Campo Vlr. Unitário não pode ser modificado. ";
            }

        /*if( ( vo.asTimestamp("DTFIM") != null && vo.asTimestamp("DTINICIO") != null )
                && vo.asTimestamp("DTFIM").compareTo(vo.asTimestamp("DTINICIO")) < 0 ){
            ErroUtils.disparaErro("Data Final deve ser maior que a data Inicial! Fineza verificar!");
        }

        if () {
            ErroUtils.disparaErro("Quantidade total das unidades ultrapassou o permitido no contrato!");
        }*/

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

    public void validaDelete() throws Exception {
        ErroUtils.disparaErro("Previsão da unidade não pode ser deletada!");
    }

    public static void setDataIncioVaga(BigDecimal numeroUnicoPrevisaoUnidade, Timestamp dataInicioUnidade, String codigoVaga) {
        listaDataIncioVaga = new HashMap<BigDecimal,Timestamp>();
        listaVagasRemanajedas = new HashMap<BigDecimal,String>();

        listaDataIncioVaga.put(numeroUnicoPrevisaoUnidade,dataInicioUnidade);
        listaVagasRemanajedas.put(numeroUnicoPrevisaoUnidade,codigoVaga);
    }
}
