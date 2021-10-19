package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.mgs.ct.dao.FilaDAO;
import com.sankhya.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsLancCustoUPGestor extends GeraFilaSuper implements GeraFila {
    public boolean executar() throws Exception {
        super.executar();

        Map<String, String> mapParametrosChave = new HashMap<String, String>();

            mapParametrosChave.put("V_CONTRATO", getParametroBigDecimal("numeroContrato").toString());//V_CONTRATO IN NUMBER
            mapParametrosChave.put("V_DTLCCUSTO", TimeUtils.formataYYYYMMDD(getParametroTimestamp("dataCusto")));//V_DTLCCUSTO IN DATE
            mapParametrosChave.put("V_UNIDADEFAT", getParametroBigDecimal("numeroUnidadeFaturamento").toString());//V_UNIDADEFAT IN NUMBER
            mapParametrosChave.put("V_TIPOFATU", getParametroBigDecimal("codigoTipoFatura").toString());//V_TIPOFATU IN NUMBER


            //CONTR_INS_LANC_CUSTO_UP_GESTOR
            String chave = geraChave(mapParametrosChave);

            FilaDAO filaDAO = new FilaDAO();
            filaDAO.incializaFila(chave, getParametroString("nomeProcessamento"));

            return true;
    }


}
