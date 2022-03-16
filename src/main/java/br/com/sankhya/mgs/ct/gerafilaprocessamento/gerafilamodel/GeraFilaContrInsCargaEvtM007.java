package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.mgs.ct.dao.FilaDAO;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsCargaEvtM007 extends GeraFilaSuper implements GeraFila {
    public  GeraFilaContrInsCargaEvtM007() {
        super();
    }
    @Override
    public boolean executar() throws Exception {
        super.executar();

        FilaDAO filaDAO = new FilaDAO();

        BigDecimal defasagem = (BigDecimal) parametrosMetrica.get("DEFASAGEM");
        String parametroPeriodoApuracao = (String)parametrosMetrica.get("PERIODOAPURACAO");
        Timestamp dataReferenciaCarga = new Timestamp(TimeUtils.add(getParametroTimestamp("dataReferencia").getTime(), defasagem.intValue(), Calendar.MONTH));

        Map<String, String> mapParametrosChave = new HashMap<String, String>();

        mapParametrosChave.put("V_CONTRATO", getParametroBigDecimal("numeroContrato").toString());
        mapParametrosChave.put("V_TP_APONTAMENTO", "SUB");
        mapParametrosChave.put("MES_CARGA", TimeUtils.getYearMonth(dataReferenciaCarga).toString());
        mapParametrosChave.put("MES_FAT", TimeUtils.getYearMonth(getParametroTimestamp("dataReferencia")).toString());
        mapParametrosChave.put("UP", getParametroBigDecimal("numeroUnidadeFaturamento").toString());
        mapParametrosChave.put("P_PARAM", parametroPeriodoApuracao );

        String chave = geraChave(mapParametrosChave);

        filaDAO.incializaFila(chave, getParametroString("nomeProcessamento"));
        return true;
    }
}
