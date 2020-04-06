package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.mgs.ct.dao.FilaDAO;

import java.util.HashMap;
import java.util.Map;

public class GeraFilaFaturaAnexo extends GeraFilaSuper implements GeraFila {
    public boolean executar() throws Exception {
        super.executar();
        setParametroExecucao("nomeProcessamento","RtnContrFaturaAnexo");

        Map<String, String> mapParametrosChave = new HashMap<String, String>();
        mapParametrosChave.put("NUFATURA", getParametroBigDecimal("numeroUnicoFatura").toString());

        String chave = geraChave(mapParametrosChave);

        FilaDAO filaDAO = new FilaDAO();
        filaDAO.setComControleTransacao(true);
        filaDAO.setCodigoUsuario(getParametroBigDecimal("codigoUsuario"));
        filaDAO.incializaFila(chave, getParametroString("nomeProcessamento"));
        return true;
    }
}
