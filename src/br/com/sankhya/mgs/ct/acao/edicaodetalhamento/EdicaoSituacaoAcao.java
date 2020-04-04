package br.com.sankhya.mgs.ct.acao.edicaodetalhamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.model.edicaodetalhamento.EdicaoSituacaoModel;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;

public class EdicaoSituacaoAcao extends EdicaoAcaoSuper implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0) {
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um registro");
        } else {

            String sitlanc = contextoAcao.getParam("SITLANC").toString();

            DynamicVO usuario = JapeFactory.dao("Usuario").findByPK(AuthenticationInfo.getCurrent().getUserID());
            String nomeusu = usuario.asString("NOMEUSU");

            for (Registro linha : linhas) {
                EdicaoSituacaoModel edicaoSituacaoModel = new EdicaoSituacaoModel();
                edicaoSituacaoModel.setParametro("SITLANC",sitlanc);
                edicaoSituacaoModel.setParametro("LOGIN",nomeusu);
                edicaoSituacaoModel.setParametro("NUEVTMENSAL",linha.getCampo("NUEVTMENSAL"));
                edicaoSituacaoModel.executar();
            }
        }
    }
}