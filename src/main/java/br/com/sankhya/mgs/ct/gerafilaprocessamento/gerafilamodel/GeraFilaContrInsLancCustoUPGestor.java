package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.dao.FilaDAO;
import com.sankhya.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsLancCustoUPGestor extends GeraFilaSuper implements GeraFila {
    public GeraFilaContrInsLancCustoUPGestor(JdbcWrapper jdbcWrapper) {
        super(jdbcWrapper);
    }

    public boolean executarFilho() throws Exception {

        System.out.println(" INICIANDO A EXECUCAO PARA INSERIR GESTOR ");

        Map<String, String> mapParametrosChave = new HashMap<String, String>();

        mapParametrosChave.put("V_CONTRATO", getParametroBigDecimal("numeroContrato").toString());//V_CONTRATO IN NUMBER
        mapParametrosChave.put("V_DTLCCUSTO", TimeUtils.formataYYYYMMDD(getParametroTimestamp("dataCusto")));//V_DTLCCUSTO IN DATE
        mapParametrosChave.put("V_UNIDADEFAT", getParametroBigDecimal("numeroUnidadeFaturamento").toString());//V_UNIDADEFAT IN NUMBER
        mapParametrosChave.put("V_TIPOFATU", getParametroBigDecimal("codigoTipoFatura").toString());//V_TIPOFATU IN NUMBER


            //CONTR_INS_LANC_CUSTO_UP_GESTOR
            String chave = geraChave(mapParametrosChave);

        FilaDAO filaDAO = new FilaDAO(this.jdbcWrapper);
            filaDAO.incializaFila(chave, getParametroString("nomeProcessamento"));

            System.out.println("FILA INICIALIZADA CHAVE =" + chave + " parametro processamento = " + getParametroString("nomeProcessamento"));
            return true;
    }


}
