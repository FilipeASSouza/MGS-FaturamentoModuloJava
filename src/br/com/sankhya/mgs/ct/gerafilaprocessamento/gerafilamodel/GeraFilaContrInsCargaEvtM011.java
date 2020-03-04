package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.mgs.ct.dao.FilaDAO;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsCargaEvtM011 extends GeraFilaSuper implements GeraFila {
    public  GeraFilaContrInsCargaEvtM011() {
        super();
    }
    @Override
    public boolean executar() throws Exception {
        super.executar();

        FilaDAO filaDAO = new FilaDAO();

        BigDecimal defasagem = (BigDecimal) parametrosMetrica.get("DEFASAGEM");
        Timestamp dataReferenciaCarga = new Timestamp(TimeUtils.add(dataReferencia.getTime(), defasagem.intValue(), Calendar.MONTH));

        Map<String, String> mapParametrosChave = new HashMap<String, String>();

        mapParametrosChave.put("V_CONTRATO", numeroContrato.toString());
        mapParametrosChave.put("V_TP_APONTAMENTO", "RSC");
        mapParametrosChave.put("MES_CARGA", TimeUtils.getYearMonth(dataReferenciaCarga).toString());
        mapParametrosChave.put("MES_FAT", TimeUtils.getYearMonth(dataReferencia).toString());
        mapParametrosChave.put("UP", numeroUnidadeFaturamento.toString());

        String chave = geraChave(mapParametrosChave);

        filaDAO.incializaFila(chave, nomeProcessamento);
        return true;
    }
}
