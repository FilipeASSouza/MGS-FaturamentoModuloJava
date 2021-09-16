package br.com.sankhya.mgs.ct.acao.edicaodetalhamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.model.edicaodetalhamento.EdicaoDataLancamentoModel;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;

import java.sql.Timestamp;

public class EdicaoDataLancamentoAcao extends EdicaoAcaoSuper implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0) {
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um registro");
        } else {

            Timestamp dtlccusto = Timestamp.valueOf(contextoAcao.getParam("DTLCCUSTO").toString());

            DynamicVO usuario = JapeFactory.dao("Usuario").findByPK(AuthenticationInfo.getCurrent().getUserID());
            String nomeusu = usuario.asString("NOMEUSU");

            for (Registro linha : linhas) {
                EdicaoDataLancamentoModel edicaoDataLancamentoModel = new EdicaoDataLancamentoModel();
                edicaoDataLancamentoModel.setParametro("DTLCCUSTO",dtlccusto);
                edicaoDataLancamentoModel.setParametro("LOGIN",nomeusu);
                edicaoDataLancamentoModel.setParametro("NUEVTMENSAL",linha.getCampo("NUEVTMENSAL"));
                edicaoDataLancamentoModel.executar();
            }
        }
    }
}
