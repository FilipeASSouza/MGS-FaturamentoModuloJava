package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.mgs.ct.dao.FilaDAO;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsLancCustoUPGestorAnexo extends GeraFilaSuper implements GeraFila {
    final Map<String, Object> parametros = new HashMap<>();

    public boolean executar() throws Exception {
        try{

            setParametroExecucao("nomeProcessamento","RtnContrInsLancCustoUPGestorAnexo");

            Map<String, String> mapParametrosChave = new HashMap<>();
            mapParametrosChave.put("NUMCONTRATO", parametros.get("numeroContrato").toString());
            mapParametrosChave.put("CODUNIDADEFATUR", parametros.get("numeroUnidadeFaturamento").toString());
            mapParametrosChave.put("DTLANCCUSTO", TimeUtils.formataYYYYMMDD(parametros.get("dataCusto")));
            mapParametrosChave.put("CODTIPOFATURA", parametros.get("codigoTipoFatura").toString());

            String chave = geraChave(mapParametrosChave);

            FilaDAO filaDAO = new FilaDAO();
            filaDAO.setComControleTransacao(true);
            filaDAO.setCodigoUsuario(new BigDecimal(parametros.get("codigoUsuario").toString()));
            filaDAO.incializaFila(chave, parametros.get("nomeProcessamento").toString());

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
        parametros.put(nome, parametro);
    }
}
