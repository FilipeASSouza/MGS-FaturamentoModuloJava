package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.mgs.ct.dao.FilaDAO;
import com.sankhya.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsLancCustoUPGestorAnexo extends GeraFilaSuper implements GeraFila {
    public boolean executar() throws Exception {
        try{

            setParametroExecucao("nomeProcessamento","RtnContrInsLancCustoUPGestorAnexo");

            Map<String, String> mapParametrosChave = new HashMap<String, String>();
            mapParametrosChave.put("NUMCONTRATO", getParametroBigDecimal("numeroContrato").toString());
            mapParametrosChave.put("CODUNIDADEFATUR", getParametroBigDecimal("numeroUnidadeFaturamento").toString());
            mapParametrosChave.put("DTLANCCUSTO", TimeUtils.formataYYYYMMDD(getParametroTimestamp("dataCusto")));
            mapParametrosChave.put("CODTIPOFATURA", getParametroBigDecimal("codigoTipoFatura").toString());


            String chave = geraChave(mapParametrosChave);

            FilaDAO filaDAO = new FilaDAO();
            filaDAO.setComControleTransacao(true);
            filaDAO.setCodigoUsuario(getParametroBigDecimal("codigoUsuario"));
            filaDAO.incializaFila(chave, getParametroString("nomeProcessamento"));

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
