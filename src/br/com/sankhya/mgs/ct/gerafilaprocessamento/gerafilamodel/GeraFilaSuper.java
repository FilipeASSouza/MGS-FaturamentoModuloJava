package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import com.sankhya.util.TimeUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class GeraFilaSuper implements GeraFila {
    protected BigDecimal numeroUnicoMetrica;
    protected BigDecimal numeroUnidadeFaturamento;
    protected Map<String, Object> parametrosMetrica;
    protected Timestamp dataReferencia;
    protected String nomeProcessamento;
    protected BigDecimal numeroContrato;

    @Override
    public boolean executar() throws Exception {
        getParametrosMetricas();
        return false;
    }

    @Override
    public String getMensagem() {
        return null;
    }

    @Override
    public void setNumeroUnicoMetrica(BigDecimal numeroUnicoMetrica) {
        this.numeroUnicoMetrica = numeroUnicoMetrica;
    }

    @Override
    public void setNumeroUnidadeFaturamento(BigDecimal numeroUnidadeFaturamento) {
        this.numeroUnidadeFaturamento = numeroUnidadeFaturamento;
    }

    @Override
    public void setDataReferencia(Timestamp dataReferencia) {
        int ajustar = dataReferencia.getDate()-1;
        Timestamp timestamp = new Timestamp(TimeUtils.add(dataReferencia.getTime(), -ajustar, Calendar.DATE));
        this.dataReferencia = timestamp;
    }

    @Override
    public void setNomeProcessamento(String nomeProcessamento) {
        this.nomeProcessamento = nomeProcessamento;
    }

    @Override
    public void setNumeroContrato(BigDecimal numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    protected void getParametrosMetricas() throws Exception {
        Collection<DynamicVO> parametroMetricaVOS = JapeFactory.dao("MGSCT_Parametro_Metrica").find("NUCONTRMETRICA = ?", numeroUnicoMetrica);


        parametrosMetrica = new HashMap<String, Object>();

        for (DynamicVO parametroMetricaVO:parametroMetricaVOS){
            String tipo = parametroMetricaVO.asString("MGSCT_Apoio_Parametro_Metrica.TIPO");
            String descricaoParametro = parametroMetricaVO.asString("MGSCT_Apoio_Parametro_Metrica.DESCRPARAM");
            String valor = parametroMetricaVO.asString("VALOR");

            Object valorConvertido = converteParametro(valor, tipo);
            parametrosMetrica.put(descricaoParametro,valorConvertido);




        }
    }

    private Object converteParametro(String valor, String tipo){
        switch(tipo){
            case "D"://Data
                return null;

            case "F"://Número Decimal
                return new BigDecimal(valor);
            case "H"://Data e Hora
                return null;
            case "I"://Número Inteiro
                return new BigDecimal(valor);
            case "S"://Texto
                return valor;
            case "T"://Hora
                return null;
        }
        return null;
    }

    protected String geraChave(Map<String, String> mapParametrosChave) {
        List<String> listaParametros = new ArrayList();

        for (String key : mapParametrosChave.keySet()) {
            String value = mapParametrosChave.get(key);
            listaParametros.add(key + "=" + value);
        }
        return StringUtils.join(listaParametros, ";");
    }
}
