package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.mgs.ct.dao.FilaDAO;
import com.sankhya.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsLancFatura extends GeraFilaSuper implements GeraFila {
    public GeraFilaContrInsLancFatura(JdbcWrapper jdbcWrapper) {
        super(jdbcWrapper);
    }
    

    @Override
    public boolean executarFilho() throws Exception {

        FilaDAO filaDAO = new FilaDAO(this.jdbcWrapper);

        Map<String, String> mapParametrosChave = new HashMap<String, String>();

        mapParametrosChave.put("V_PERIODOFAT", TimeUtils.formataYYYYMMDD(getParametroTimestamp("dataReferencia")));       //IN DATE, DD/MM/YYYY
        mapParametrosChave.put("V_DATAEMISSAO", TimeUtils.formataYYYYMMDD(getParametroTimestamp("dataEmissao")));      //    IN DATE, DD/MM/YYYY
        mapParametrosChave.put("V_DATAVENCIMENTO", TimeUtils.formataYYYYMMDD(getParametroTimestamp("dataVencimento")));   //  IN DATE, DD/MM/YYYY
        mapParametrosChave.put("V_DTLCCUSTO", TimeUtils.formataYYYYMMDD(getParametroTimestamp("dataCusto")));        //         IN DATE, DD/MM/YYYY
        mapParametrosChave.put("V_CODTIPOFATURA", getParametroBigDecimal("codigoTipoFatura").toString());    //      IN NUMBER,
        mapParametrosChave.put("V_CODUNIDADEFATURA", getParametroBigDecimal("codigoUnidadeFaturamento").toString()); //  IN NUMBER,
        mapParametrosChave.put("V_APROVADAS", getParametroString("aprovadas"));        //       IN VARCHAR2, OBRIGATóRIO (“T” TODAS, “S” Só AS APROVADAS)

        String chave = geraChave(mapParametrosChave);

        filaDAO.incializaFila(chave, getParametroString("nomeProcessamento"));
        return true;
    }


}
