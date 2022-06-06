package br.com.sankhya.mgs.ct.validator;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;

import java.math.BigDecimal;

public class PrevisaoContrato {

    //Nenhuma altera??o realizada na classe Previs?oContrato
    private DynamicVO vo;
    private String regraVadalicao = "";

    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
    }

    public String getRegraValidacao() throws Exception {
        if (regraVadalicao.equals("")) {
            DynamicVO eventoVO = JapeFactory.dao("TGFECUS").findByPK(vo.asBigDecimal("CODEVENTO"));
            String tipoEvento = eventoVO.asString("TIPOEVENTO");

            BigDecimal codigoControle = vo.asBigDecimal("CODCONTROLE");
            if (codigoControle == null) {
                ErroUtils.disparaErro("Controle deve ser preenchido para esse tipo de evento!");
            }

            regraVadalicao = tipoEvento.concat(codigoControle.toString());

            if (regraVadalicao == null) {
                regraVadalicao = "";
            }
        }

        ErroUtils.disparaErro(regraVadalicao);
        return regraVadalicao;
    }
}
