package br.com.sankhya.mgs.ct.acao.edicaodetalhamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.model.edicaodetalhamento.EdicaoSituacaoModel;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;

public class EdicaoSituacaoAcao extends EdicaoAcaoSuper implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();

        DynamicVO usuario = JapeFactory.dao("Usuario").findByPK(AuthenticationInfo.getCurrent().getUserID());
        String codusu = usuario.asString("NOMEUSU");

        if (linhas.length == 0) {
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um registro");
        } else {

            BigDecimal sitlanc = BigDecimal.valueOf(Long.parseLong(contextoAcao.getParam("SITLANC").toString()));

            for (Registro linha : linhas) {

                    if( linha.getCampo("INTEGRACAO_LANC") == null ){
                        linha.setCampo("TIPO_EVENTO", String.valueOf("M") );
                        linha.setCampo("SITUACAO_LANC", sitlanc );
                        linha.setCampo("DHUPD", TimeUtils.getNow() );
                        linha.setCampo("USUINS", codusu );
                        linha.save();
                    }

                    /*Descontinuado devido ao erro no cursor

                    NativeSqlDecorator atualizarDetalhamentoSQL = new NativeSqlDecorator("UPDATE MGSTCTEVTMENSAL SET TIPLANCEVENTO = 'M', CODSITLANC = :V_SIT, DTALTERLANC = SYSDATE, CODUSUALTERLANC = :LOGIN WHERE NUEVTMENSAL = :V_NUEVTMENSAL ");
                    atualizarDetalhamentoSQL.setParametro("V_SIT", sitlanc);
                    atualizarDetalhamentoSQL.setParametro("LOGIN", codusu);
                    atualizarDetalhamentoSQL.setParametro("V_NUEVTMENSAL", linha.getCampo("NUEVTMENSAL"));
                    atualizarDetalhamentoSQL.atualizar();

                    EdicaoSituacaoModel edicaoSituacaoModel = new EdicaoSituacaoModel();
                    edicaoSituacaoModel.setParametro("SITLANC",sitlanc);
                    edicaoSituacaoModel.setParametro("LOGIN",codusu);
                    edicaoSituacaoModel.setParametro("NUEVTMENSAL",linha.getCampo("NUEVTMENSAL"));
                    edicaoSituacaoModel.executar();
                    */

            }
        }
    }
}

