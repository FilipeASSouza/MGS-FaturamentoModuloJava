package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.dao.FilaDAO;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsCargaEvtM021 extends GeraFilaSuper implements GeraFila {
    public GeraFilaContrInsCargaEvtM021(JdbcWrapper jdbcWrapper) {
        super(jdbcWrapper);
    }

    @Override
    public boolean executarFilho() throws Exception {

        FilaDAO filaDAO = new FilaDAO(this.jdbcWrapper);

        BigDecimal defasagem = (BigDecimal) parametrosMetrica.get("DEFASAGEM");
        Timestamp dataReferenciaCarga = new Timestamp(TimeUtils.add(getParametroTimestamp("dataReferencia").getTime(), defasagem.intValue(), Calendar.MONTH));

        Map<String, String> mapParametrosChave = new HashMap<String, String>();

        mapParametrosChave.put("V_CONTRATO", getParametroBigDecimal("numeroContrato").toString());
        mapParametrosChave.put("V_TP_APONTAMENTO", "MAT");
        mapParametrosChave.put("MES_CARGA", TimeUtils.getYearMonth(dataReferenciaCarga).toString());
        mapParametrosChave.put("MES_FAT", TimeUtils.getYearMonth(getParametroTimestamp("dataReferencia")).toString());
        mapParametrosChave.put("UP", getParametroBigDecimal("numeroUnidadeFaturamento").toString());

        String chave = geraChave(mapParametrosChave);

        filaDAO.incializaFila(chave, getParametroString("nomeProcessamento"));
        return true;
    }
}
