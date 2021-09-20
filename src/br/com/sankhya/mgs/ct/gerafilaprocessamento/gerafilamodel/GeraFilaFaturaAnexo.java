package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.dao.FilaDAO;

import java.util.HashMap;
import java.util.Map;

public class GeraFilaFaturaAnexo extends GeraFilaSuper implements GeraFila {
    public GeraFilaFaturaAnexo(JdbcWrapper jdbcWrapper) {
        super(jdbcWrapper);
    }
    public boolean executarFilho() throws Exception {
        setParametroExecucao("nomeProcessamento","RtnContrFaturaAnexo");

        Map<String, String> mapParametrosChave = new HashMap<String, String>();
        mapParametrosChave.put("NUFATURA", getParametroBigDecimal("numeroUnicoFatura").toString());

        String chave = geraChave(mapParametrosChave);

        FilaDAO filaDAO = new FilaDAO(this.jdbcWrapper);
        filaDAO.setCodigoUsuario(getParametroBigDecimal("codigoUsuario"));
        filaDAO.incializaFila(chave, getParametroString("nomeProcessamento"));
        return true;
    }
}
