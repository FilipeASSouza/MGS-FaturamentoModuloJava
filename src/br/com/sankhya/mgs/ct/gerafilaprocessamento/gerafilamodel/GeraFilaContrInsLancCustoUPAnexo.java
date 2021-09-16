package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.mgs.ct.dao.FilaDAO;
import com.sankhya.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsLancCustoUPAnexo extends GeraFilaSuper implements GeraFila {
    public boolean executarFilho() throws Exception {
        
        setParametroExecucao("nomeProcessamento","RtnContrInsLancCustoUPAnexo");

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
        return true;
    }
}
