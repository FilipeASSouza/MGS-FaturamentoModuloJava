package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.mgs.ct.dao.FilaDAO;
import com.sankhya.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsLancCustoUPGestorAnexo implements GeraFila {
    public boolean executar() throws Exception {
        try{

            GeraFilaSuper geraFilaSuper = new GeraFilaSuper();
            geraFilaSuper.executar();
            setParametroExecucao("nomeProcessamento","RtnContrInsLancCustoUPGestorAnexo");

            Map<String, String> mapParametrosChave = new HashMap<String, String>();
            mapParametrosChave.put("NUMCONTRATO", geraFilaSuper.getParametroBigDecimal("numeroContrato").toString());
            mapParametrosChave.put("CODUNIDADEFATUR", geraFilaSuper.getParametroBigDecimal("numeroUnidadeFaturamento").toString());
            mapParametrosChave.put("DTLANCCUSTO", TimeUtils.formataYYYYMMDD(geraFilaSuper.getParametroTimestamp("dataCusto")));
            mapParametrosChave.put("CODTIPOFATURA", geraFilaSuper.getParametroBigDecimal("codigoTipoFatura").toString());


            String chave = geraFilaSuper.geraChave(mapParametrosChave);

            FilaDAO filaDAO = new FilaDAO();
            filaDAO.setComControleTransacao(true);
            filaDAO.setCodigoUsuario(geraFilaSuper.getParametroBigDecimal("codigoUsuario"));
            filaDAO.incializaFila(chave, geraFilaSuper.getParametroString("nomeProcessamento"));

        }catch(Exception e){
            System.out.println("ERRO AO INICIA A FILA GeraFilaContrInsLancCustoUPGestorAnexo" + e);
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public String getMensagem() {
        return null;
    }

    @Override
    public void setParametroExecucao(String nome, Object parametro) {

    }
}
