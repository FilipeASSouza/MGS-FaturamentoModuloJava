package br.com.sankhya.mgs.ct.model.edicaodetalhamento;


import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.util.Arrays;

public class EdicaoSituacaoModel extends EdicaoDetalhamentoModelSuper{
    private JapeWrapper detalhamentoCustoDAO = JapeFactory.dao("MGSCT_Detalhamento_Custo");
    private int indiceInicial = 0;
    private int indiceFinal = 0;
    
    public EdicaoSituacaoModel(JdbcWrapper jdbc) throws Exception {
        super("updateSituacao.sql", jdbc);
    }
    
    public void executar() throws Exception {
        super.inicializarExecutar();
        nativeSqlDecorator.setParametro("V_SIT", BigDecimal.valueOf(Long.parseLong(parametros.get("SITLANC").toString())));//0 é ativo, 1 é inativo
        super.finalizaExecutar();
    }

    public void atualizarSituacao(Registro[] linhas, BigDecimal sitlanc ) throws Exception{

        listaDinamicaArray(linhas, sitlanc);
    }

    private void atualizaRegistros(BigDecimal sitlanc, String codusu, Registro[] linhas) throws Exception {

        for(Registro linha : linhas ){
            DynamicVO detalhamentoCustoVO = detalhamentoCustoDAO.findOne("NUEVTMENSAL = ? AND CODINTEGRACAOLC IS NULL", new Object[]{linha.getCampo("NUEVTMENSAL")});
            FluidUpdateVO detalhamentoCustoFUVO = detalhamentoCustoDAO.prepareToUpdate(detalhamentoCustoVO);
            detalhamentoCustoFUVO.set("TIPLANCEVENTO", String.valueOf("M"));
            detalhamentoCustoFUVO.set("CODSITLANC", sitlanc);
            detalhamentoCustoFUVO.set("DTALTERLANC", TimeUtils.getNow());
            detalhamentoCustoFUVO.set("CODUSUALTERLANC", codusu);
            detalhamentoCustoFUVO.update();

            indiceInicial = indiceFinal + 1;
            indiceFinal = indiceFinal + linhas.length;
        }
    }

    public void listaDinamicaArray(Registro[] arrays, BigDecimal sitlanc) throws Exception{

        int quantidadeRegistro = arrays.length;
        DynamicVO usuario = JapeFactory.dao("Usuario").findByPK(AuthenticationInfo.getCurrent().getUserID());
        String codusu = usuario.asString("NOMEUSU");

        if(quantidadeRegistro < 2000 ){
            atualizaRegistros(sitlanc, codusu, arrays);
        }else{
            // verificando se a quantidade é menor que 10 mil registros
            if( quantidadeRegistro < 10000 ){
                // processando a lista
                for(int i = 1; i <= 4; i++){
                    Registro[] listaDinamica = Arrays.copyOfRange( arrays, indiceInicial, indiceFinal == 0 ? 2500 : indiceFinal );
                    atualizaRegistros( sitlanc, codusu, listaDinamica );
                }
            }
        }
    }
}
