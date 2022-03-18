package br.com.sankhya.mgs.ct.acao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.model.RemanejarVagaUnidadeModel;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static br.com.sankhya.bh.utils.ErroUtils.disparaErro;

public class RemanejarVagaAcao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0) {
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um registro");
        } else {
            Timestamp dataFechamentoVaga = Timestamp.valueOf(contextoAcao.getParam("DTFECHA").toString());
            BigDecimal codigoUnidadeDestino = new BigDecimal(contextoAcao.getParam("CODSITE").toString());
            DynamicVO siteVO = JapeFactory.dao("Site").findByPK(codigoUnidadeDestino);
            if (siteVO == null){
                disparaErro("Unidade de faturamento não encontrada");
            }


            for (Registro linha : linhas) {
                BigDecimal numeroUnicoVagaPrevisaoUnidade = new BigDecimal(linha.getCampo("NUUNIDPREVVAGA").toString());
                RemanejarVagaUnidadeModel remanejarVagaUnidadeModel = new RemanejarVagaUnidadeModel();
                remanejarVagaUnidadeModel.setCodigoUnidadeDestino(codigoUnidadeDestino);
                remanejarVagaUnidadeModel.setDataFechamentoVaga(dataFechamentoVaga);
                remanejarVagaUnidadeModel.setNumeroUnicoVagaPrevisaoUnidade(numeroUnicoVagaPrevisaoUnidade);
                remanejarVagaUnidadeModel.transferir();
            }

        }
    }
}
