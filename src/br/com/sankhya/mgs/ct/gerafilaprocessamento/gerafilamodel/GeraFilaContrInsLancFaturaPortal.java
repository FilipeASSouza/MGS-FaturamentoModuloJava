package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.mgs.ct.dao.FilaDAO;

import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsLancFaturaPortal extends GeraFilaSuper implements GeraFila {
    public GeraFilaContrInsLancFaturaPortal() {
        super();
    }

    @Override
    public boolean executarFilho() throws Exception {
        

        FilaDAO filaDAO = new FilaDAO();

        Map<String, String> mapParametrosChave = new HashMap<String, String>();

        mapParametrosChave.put("V_NUM_FATURA", getParametroBigDecimal("numeroFatura").toString());

        String chave = geraChave(mapParametrosChave);

        filaDAO.incializaFila(chave, getParametroString("nomeProcessamento"));
        return true;
    }


}
