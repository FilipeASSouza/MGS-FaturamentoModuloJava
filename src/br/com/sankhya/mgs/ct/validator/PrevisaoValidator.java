package br.com.sankhya.mgs.ct.validator;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;

import java.math.BigDecimal;

public class PrevisaoValidator {
    private DynamicVO vo;
    private String regraVadalicao = "";

    public void setVo(DynamicVO vo) throws Exception {
        this.vo = vo;
    }

    public void validaDadosInsert() throws Exception {
        boolean postoPreechido = !(vo.asBigDecimalOrZero("CODTIPOPOSTO").equals(BigDecimal.ZERO));
        boolean servicoMaterialPreechido = !(vo.asBigDecimalOrZero("CODSERVMATERIAL").equals(BigDecimal.ZERO));
        boolean quantidadePreenchido = !(vo.asBigDecimalOrZero("QTDCONTRATADA").equals(BigDecimal.ZERO));
        boolean valorPreechido = !(vo.asBigDecimalOrZero("VLRUNITARIO").equals(BigDecimal.ZERO));

        String regraValidacao = getRegraValidacao();
        if ("".equals(regraValidacao)) {
            ErroUtils.disparaErro("Evento sem Tipo de Evento configurando não pode ser usado");
        }
        String erro = "";
        switch (regraValidacao) {
            case "P"://posto
                if (!postoPreechido)
                    erro += "Posto deve ser preenchido. ";
                if (servicoMaterialPreechido)
                    erro += "Serviço/Material não pode ser preenchido. ";
                if (!quantidadePreenchido)
                    erro += "Quantidade deve ser preenchida. ";
                if (valorPreechido)
                    erro += "Valor não pode ser preenchido. ";
                break;
            case "C"://contrato
                if (postoPreechido)
                    erro += "Posto não deve ser preenchido. ";
                if (servicoMaterialPreechido)
                    erro += "Serviço/Material não pode ser preenchido. ";
                if (quantidadePreenchido)
                    erro += "Quantidade não pode ser preenchida. ";
                if (!valorPreechido)
                    erro += "Valor deve ser preenchido. ";
                break;
            case "R"://rescisao
                if (postoPreechido)
                    erro += "Posto não deve ser preenchido. ";
                if (servicoMaterialPreechido)
                    erro += "Serviço/Material não pode ser preenchido. ";
                if (quantidadePreenchido)
                    erro += "Quantidade não pode ser preenchida. ";
                if (valorPreechido)
                    erro += "Valor não pode ser preenchido. ";
                break;
            case "S1"://serviceo/material controle 1
            case "S2"://serviceo/material controle 2
                if (postoPreechido)
                    erro += "Posto não deve ser preenchido. ";
                if (!servicoMaterialPreechido)
                    erro += "Serviço/Material deve ser preenchido. ";
                if (!valorPreechido)
                    erro += "Valor deve ser preenchido. ";
                break;
            case "S3"://serviceo/material controle 3
            case "S4"://serviceo/material controle 4
                if (postoPreechido)
                    erro += "Posto não deve ser preenchido. ";
                if (!servicoMaterialPreechido)
                    erro += "Serviço/Material deve ser preenchido. ";
                if (!quantidadePreenchido)
                    erro += "Quantidade deve ser preenchida. ";
                if (valorPreechido)
                    erro += "Valor nao pode ser preenchido. ";
                break;
            default:
                erro = "Regra não definida para Tipo de Evento: " + regraValidacao;
        }
        if (!"".equals(erro)) {
            ErroUtils.disparaErro(erro);
        }

        if (postoPreechido && servicoMaterialPreechido) {
            ErroUtils.disparaErro("Campos Tipos do Posto e Serviço/Material não podem ser preenchidos no mesmo lançamento!");
        }
    }

    public String getRegraValidacao() throws Exception {
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
}
