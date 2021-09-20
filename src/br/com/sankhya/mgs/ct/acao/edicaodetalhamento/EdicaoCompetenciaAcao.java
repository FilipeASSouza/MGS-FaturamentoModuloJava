package br.com.sankhya.mgs.ct.acao.edicaodetalhamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.model.edicaodetalhamento.EdicaoCompetenciaModel;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class EdicaoCompetenciaAcao extends EdicaoAcaoSuper implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();
        if (linhas.length == 0) {
            contextoAcao.setMensagemRetorno("Favor seleciona pelo menos um registro");
        } else {
            BigDecimal compfatu = TimeUtils.getYearMonth(Timestamp.valueOf(contextoAcao.getParam("COMPFATU").toString()));
            Timestamp dtlccusto = Timestamp.valueOf(contextoAcao.getParam("DTLCCUSTO").toString());

            DynamicVO usuario = JapeFactory.dao("Usuario").findByPK(AuthenticationInfo.getCurrent().getUserID());
            String nomeusu = usuario.asString("NOMEUSU");
            JdbcWrapper jdbcWrapper = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
            for (Registro linha : linhas) {
                if( linha.getCampo("INTEGRACAO_LANC") == null ){
                EdicaoCompetenciaModel edicaoCompetenciaModel = new EdicaoCompetenciaModel(jdbcWrapper);
                edicaoCompetenciaModel.setParametro("COMPFATU",compfatu);
                edicaoCompetenciaModel.setParametro("DTLCCUSTO",dtlccusto);
                edicaoCompetenciaModel.setParametro("LOGIN",nomeusu);
                edicaoCompetenciaModel.setParametro("NUEVTMENSAL",linha.getCampo("NUEVTMENSAL"));
                edicaoCompetenciaModel.executar();
            }
        }
    }
}
}
