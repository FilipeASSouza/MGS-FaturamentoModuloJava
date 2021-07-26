package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.mgs.ct.dao.FilaDAO;
import com.sankhya.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsLancCustoUPGestorAnexo extends GeraFilaSuper implements GeraFila {
    public boolean executar() throws Exception {
        super.executar();
        setParametroExecucao("nomeProcessamento","RtnContrInsLancCustoUPGestorAnexo");


        Map<String, String> mapParametrosChave = new HashMap<String, String>();
        mapParametrosChave.put("NUMCONTRATO", getParametroBigDecimal("numeroContrato").toString());
        mapParametrosChave.put("CODUNIDADEFATUR", getParametroBigDecimal("numeroUnidadeFaturamento").toString());
        mapParametrosChave.put("DTLANCCUSTO", TimeUtils.formataYYYYMMDD(getParametroTimestamp("dataCusto")));
        mapParametrosChave.put("CODTIPOFATURA", getParametroBigDecimal("codigoTipoFatura").toString());


        String chave = geraChave(mapParametrosChave);

        System.out.println("EXECUTANDO GeraFilaContrInsLancCustoUPGestorAnexo CHAVE = " + chave);

        FilaDAO filaDAO = new FilaDAO();
        filaDAO.setComControleTransacao(true);
        filaDAO.setCodigoUsuario(getParametroBigDecimal("codigoUsuario"));
        filaDAO.incializaFila(chave, getParametroString("nomeProcessamento"));
        return true;
    }
}
