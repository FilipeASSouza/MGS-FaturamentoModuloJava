package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.bh.utils.ErroUtils;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.model.ApoioVagasModel;
import br.com.sankhya.mgs.ct.model.VagasPrevisaoContratoModel;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

public class GerarVagasPrevisaoContrato implements AcaoRotinaJava {

    private DynamicVO mestrevo;

    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();

        if (linhas.length == 0){
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos uma linha na previs�o do contrato");
        } else {
            /*
            Objetivo: Utilizar na tela de previs�o do contrato ao selecionar
            varias previs�es para cria��o de vagas em caso de um contrato muito grande
             */

            for( Registro linha : linhas ){
            mestrevo = JapeFactory.dao("MGSCT_Modalidade_Contrato").findByPK((BigDecimal) linha.getCampo("NUMODALIDADE"));

                //BigDecimal codigoTipoPosto = BigDecimal.valueOf(Long.parseLong(linha.getCampo("CODTIPOPOSTO").toString()));
                BigDecimal codigoTipoPosto = (BigDecimal) linha.getCampo("CODTIPOPOSTO");
                boolean postoPreechido = !(codigoTipoPosto.equals(BigDecimal.ZERO));
                if (postoPreechido) {
                    DynamicVO tgfpssVO = JapeFactory.dao("TGFPSS").findByPK(codigoTipoPosto);
                    String prefixoposto = tgfpssVO.asString("PREFIXOPOSTO");
                    ArrayList<DynamicVO> vagaVOs = criaVagas(prefixoposto, (BigDecimal) linha.getCampo("QTDCONTRATADA"), (BigDecimal) linha.getCampo("NUCONTRPREV"));
                    criaPrevisaoVagas(vagaVOs, (BigDecimal) linha.getCampo("NUCONTRPREV"));
                }
            }
        }
    }

    public ArrayList<DynamicVO> criaVagas(String sigla, BigDecimal quantidadeContratadaLinha, BigDecimal numeroUnicoPrevisao) throws Exception {

        BigDecimal quantidadeContratada = quantidadeContratadaLinha;
        BigDecimal numeroUnicoPreviesoContrato = numeroUnicoPrevisao;


        BigDecimal quantidadeVagasAtivas = new VagasPrevisaoContratoModel().quantidadeVagasAtivas(numeroUnicoPreviesoContrato, sigla);

        if (quantidadeContratada.compareTo(quantidadeVagasAtivas) < 0) {
            ErroUtils.disparaErro("A quantidade de vagas n�o pode ser diminuida!");
        }

        BigDecimal quantidadeCriarNovasVagas = quantidadeContratada.subtract(quantidadeVagasAtivas);


        ArrayList<DynamicVO> dynamicVOS = new ApoioVagasModel().criaVagas(quantidadeCriarNovasVagas, sigla);

        return dynamicVOS;

    }

    public void criaPrevisaoVagas(ArrayList<DynamicVO> vagaVOs, BigDecimal numeroUnicoPrevisao) throws Exception {
        VagasPrevisaoContratoModel vagasPrevisaoContratoModel = new VagasPrevisaoContratoModel();
        for (DynamicVO vagaVO : vagaVOs) {
            BigDecimal numeroUnicoPrevisoesContrato = numeroUnicoPrevisao;
            String codigoVaga = vagaVO.asString("CODVAGA");
            Timestamp dataInicio = mestrevo.asTimestamp("MGSCT_Dados_Contrato.DTINICIO");
            vagasPrevisaoContratoModel.criar(
                    numeroUnicoPrevisoesContrato,
                    codigoVaga,
                    dataInicio
            );
        }
    }
}
