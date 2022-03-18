package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.mgs.ct.dao.FilaDAO;
import com.sankhya.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class GeraFilaContrInsLancCustoUP extends GeraFilaSuper implements GeraFila {
    public boolean executar() throws Exception {
        super.executar();

        Map<String, String> mapParametrosChave = new HashMap<String, String>();

        if (validaInsereFilaProcessamento()) {
            mapParametrosChave.put("V_CONTRATO", getParametroBigDecimal("numeroContrato").toString());//V_CONTRATO IN NUMBER
            mapParametrosChave.put("V_MODALIDADE", getParametroBigDecimal("numeroUnicoModalidade").toString());//V_MODALIDADE IN NUMBER
            mapParametrosChave.put("V_MESFATURAMENTO", TimeUtils.getYearMonth(getParametroTimestamp("dataReferencia")).toString());//V_MESFATURAMENTO IN NUMBER
            mapParametrosChave.put("V_DTLCCUSTO", TimeUtils.formataYYYYMMDD(getParametroTimestamp("dataCusto")));//V_DTLCCUSTO IN DATE
            mapParametrosChave.put("V_TIPOFATU", getParametroBigDecimal("codigoTipoFatura").toString());//V_TIPOFATU IN NUMBER
            mapParametrosChave.put("V_UNIDADEFAT", getParametroBigDecimal("numeroUnidadeFaturamento").toString());//V_UNIDADEFAT IN NUMBER

            //CONTR_INS_LANC_CUSTO_UP
            String chave = geraChave(mapParametrosChave);

            FilaDAO filaDAO = new FilaDAO();

            filaDAO.incializaFila(chave, getParametroString("nomeProcessamento"));
            return true;
        } else {
            return false;
        }
    }

    private boolean validaInsereFilaProcessamento () throws Exception {
        DynamicVO tipoProcessamentoVO = JapeFactory
                .dao("MGSCT_Tipo_Processamento")
                .findOne("NOME = ?", getParametroString("nomeProcessamento"));

        if (tipoProcessamentoVO == null) {
            return false;
        }

        /*
        MGSTCTINTEGRADC
        Motivo: Retirado para quando houver somente lançamentos manuais no detalhamento, ir para planilha de fiscal.
        29/07/2021

        JapeWrapper integrDetalhaCustoDAO = JapeFactory.dao("MGSCT_Integr_Detalha_Custo");
        DynamicVO integrDetalhaCustoVO = integrDetalhaCustoDAO.findOne("NUMCONTRATO = ? and CODUNIDADEFATUR = ? and INTCOMPETENCIA = ?",
                getParametroBigDecimal("numeroContrato"),
                getParametroBigDecimal("numeroUnidadeFaturamento"),
                TimeUtils.getYearMonth(getParametroTimestamp("dataReferencia"))
        );
        if (integrDetalhaCustoVO == null) {
            return false;
        }
        */

        return true;
    }
}
