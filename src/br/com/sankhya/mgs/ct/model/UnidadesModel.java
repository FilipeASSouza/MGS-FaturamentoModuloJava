package br.com.sankhya.mgs.ct.model;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;

import java.math.BigDecimal;
import java.util.HashMap;

public class UnidadesModel {
    private JapeWrapper dao = JapeFactory.dao("MGSCT_Unidades");
    private DynamicVO vo;

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

    private void inicialzaVariaveis() throws Exception {
    }

    private void validaDadosInsert() throws Exception {
    }

    private void validaDadosUpdate() throws Exception {
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
