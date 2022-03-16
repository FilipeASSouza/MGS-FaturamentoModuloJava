package br.com.sankhya.mgs.ct.controller;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;

public class LocalContratoController {
    public void doActionBuscaEnderecoParceiro(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if(linhas.length>1){
            ErroUtils.disparaErro("Selecione somente um registro.");
        }

        Registro linha = linhas[0];
        BigDecimal codigoParceiro = new BigDecimal(linha.getCampo("CODPARC").toString());
        JapeWrapper parceiroDAO = JapeFactory.dao("Parceiro");
        DynamicVO parceiroVO = parceiroDAO.findByPK(codigoParceiro);
        String cepParceiro = parceiroVO.asString("CEP");
        BigDecimal codigoCidadeParceiro = parceiroVO.asBigDecimal("CODCID");
        BigDecimal codigoBairroParceiro = parceiroVO.asBigDecimal("CODBAI");
        BigDecimal codigoEnderecoParceiro = parceiroVO.asBigDecimal("CODEND");
        String numeroEnderecoParceiro = parceiroVO.asString("NUMEND");
        String complementoEnderecoParceiro = parceiroVO.asString("COMPLEMENTO");
        BigDecimal codigoUF = parceiroVO.asBigDecimal("Cidade.UF");
        linha.setCampo("CEP", cepParceiro);
        linha.setCampo("CODCIDNF", codigoCidadeParceiro);
        linha.setCampo("CODBAI", codigoBairroParceiro);
        linha.setCampo("CODEND", codigoEnderecoParceiro);
        linha.setCampo("NUMERO", numeroEnderecoParceiro);
        linha.setCampo("COMPLEMENTO", complementoEnderecoParceiro);
        linha.setCampo("CODUF", codigoUF);
        linha.save();
    }
}
