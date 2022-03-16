package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;

public class UnidadesModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Unidades");
    private DynamicVO vo;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private DynamicVO contratoVO;

    public UnidadesModel() {
    }

    public UnidadesModel(BigDecimal numeroUnico) throws Exception {
        this.vo = this.dao.findByPK(new Object[]{numeroUnico});
        this.inicialzaVariaveis();
    }

    public UnidadesModel(DynamicVO dynamicVO) throws Exception {
        this.vo = dynamicVO;
        this.inicialzaVariaveis();
    }

    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
        this.inicialzaVariaveis();
    }

    public void inicialzaVariaveis() throws Exception {
        contratoVO = JapeFactory.dao("MGSCT_Dados_Contrato").findByPK(vo.asBigDecimalOrZero("NUMCONTRATO"));
    }

    public void validaDadosInsert() throws Exception {
        BigDecimal numeroContrato = vo.asBigDecimalOrZero("NUMCONTRATO");

        validaDataLancamento();
        validaDataUnidadeContrato(numeroContrato);
    }

    public void validaDadosUpdate() throws Exception {

        BigDecimal numeroContrato = vo.asBigDecimalOrZero("NUMCONTRATO");

        validaDataLancamento();
        validaDataUnidadeContrato(numeroContrato);
    }

    public void validaDataLancamento() throws Exception {
        Timestamp dataInicioUnidade = vo.asTimestamp("DTINICIO");
        Timestamp dataFimUnidade = vo.asTimestamp("DTFIM");
        if( dataFimUnidade != null ){
            dataFimUnidade = vo.asTimestamp("DTFIM");
        }
        String dataInicioContrato = sdf.format(contratoVO.asTimestamp("DTINICIO"));

        /*
        Desenvolvimento: 15/03/2022
        1- Valida a Data Inicio da Unidade menor que a Data Fim, definido para ser a partir do dia, permitindo ser no mesmo dia
        2- Valida a Data Inicio da Unidade ser menor que a Data Inicio Contrato
         */
        if(vo.asTimestamp("DTINICIO").compareTo(contratoVO.asTimestamp("DTINICIO")) < 0 ){
            ErroUtils.disparaErro("Data Inicio da Unidade: "+ sdf.format(dataInicioUnidade)
                    + " não pode ser inferior a Data Inicio do Contrato: "+ sdf.format(dataInicioContrato) +" !" );
        }else if( dataFimUnidade != null ){
            if(vo.asTimestamp("DTFIM").compareTo(vo.asTimestamp("DTINICIO")) < 0){
                ErroUtils.disparaErro("Data Final: "+ sdf.format(dataFimUnidade)
                        + " não pode ser inferior a Data Inicio: "+ sdf.format(dataInicioUnidade) +" !" );
            }
        }
    }

    public void validaDataUnidadeContrato(BigDecimal numeroContrato ) throws Exception {
        Timestamp dataInicioUnidade = vo.asTimestamp("DTINICIO");
        Timestamp dataFimUnidade = vo.asTimestamp("DTFIM");
        BigDecimal codUnidadeFilha = vo.asBigDecimalOrZero("CODSITE");

        Collection<DynamicVO> unidadesVO = dao.find("NUMCONTRATO =? AND CODSITE = ?"
                , numeroContrato, codUnidadeFilha );
        for(DynamicVO unidade : unidadesVO ){

            Timestamp dataInicioOutraUnidade = unidade.asTimestamp("DTINICIO");
            Timestamp dataFimOutraUnidade = unidade.asTimestamp("DTFIM");

            if( unidade.asTimestamp("DTFIM") == null ) {
                ErroUtils.disparaErro("Ja existe um registro: "+unidade.asBigDecimalOrZero("NUCONTRCENT")
                +" em aberto, gentileza verificar!");
            }else if( dataInicioOutraUnidade.equals(dataInicioUnidade) ){
                ErroUtils.disparaErro("Ja existe um registro: "+ unidade.asBigDecimalOrZero("NUCONTRCENT")
                + " com a mesma data Inicio, gentileza verificar!");
            }else if(dataInicioUnidade.compareTo(dataFimOutraUnidade) < 0){
                ErroUtils.disparaErro("Data Inicio: "+sdf.format(dataInicioUnidade)
                        +" não pode ser menor que Data Fim: "+ sdf.format(dataFimOutraUnidade) +
                        " do mesmo contrato: "+ numeroContrato +" da mesma unidade, gentileza verificar!");
            }else if(dataInicioUnidade.equals(dataFimOutraUnidade)){
                ErroUtils.disparaErro("Data Inicio: "+sdf.format(dataInicioUnidade)
                + " não pode ser igual a Data Fim: "+sdf.format(dataFimOutraUnidade)
                + " do mesmo contrato: "+ numeroContrato + " da mesma unidade, gentileza verificar!");
            }

        }
    }

    private void validaDadosUpdate(DynamicVO oldvo) throws Exception {
    }

    public void preecheCamposCalculados() throws Exception {
        this.vo.setProperty("LIBERADOFATURAMENTO", "S");
    }

    public void criaRegistrosDerivados() throws Exception {
        this.acertarCodigoSitePai();
    }

    private void acertarCodigoSitePai() throws Exception {
        BigDecimal codsite = this.vo.asBigDecimal("CODSITE");
        BigDecimal codigoSitePaiTela = this.vo.asBigDecimal("CODSITEPAI");
        JapeWrapper siteDAO = JapeFactory.dao("Site");
        DynamicVO siteVO = siteDAO.findByPK(new Object[]{codsite});
        BigDecimal codigoSitePaiEntidade = siteVO.asBigDecimalOrZero("CODSITEPAI");
        if (codigoSitePaiEntidade.compareTo(codigoSitePaiTela) != 0) {
            FluidUpdateVO siteFUVO = siteDAO.prepareToUpdate(siteVO);
            siteFUVO.set("CODSITEPAI", codigoSitePaiTela);
            siteFUVO.update();
        }

    }

    private void validaDelete() throws Exception {
    }

    private void validaCamposUpdate(HashMap<String, Object[]> campos) throws Exception {
        String mensagemErro = "";
        if (campos.containsKey("#CAMPO#")) {
            mensagemErro = mensagemErro + "Campo Evento não pode ser modificado. ";
        }

        if (mensagemErro != "") {
            ErroUtils.disparaErro(mensagemErro);
        }

    }
}
