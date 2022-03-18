package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import br.com.lugh.performance.PerformanceMonitor;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import com.sankhya.util.TimeUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public abstract class GeraFilaSuper implements GeraFila {

    protected Map<String, Object> parametrosMetrica;
    protected Map<String, Object> parametrosExecucao = new HashMap<>();
    protected JdbcWrapper jdbcWrapper;

    public GeraFilaSuper(JdbcWrapper jdbc) {
        this.jdbcWrapper = jdbc;
    }

    public boolean executar() throws Exception {
        return PerformanceMonitor.INSTANCE.measureReturnJava("GeraFilaSuper" + getClass().getName(), null, () -> {
            getParametrosMetricas();
            return executarFilho();
        });
    }


    public abstract boolean executarFilho() throws Exception;


    @Override
    public String getMensagem() {
        return null;
    }

    @Override
    public void setParametroExecucao(String nome, Object parametro) {
        if ("dataReferencia".equals(nome)) {
            Timestamp dataReferencia = (Timestamp) parametro;
            int ajustar = dataReferencia.getDate() - 1;
            dataReferencia = new Timestamp(TimeUtils.add(dataReferencia.getTime(), -ajustar, Calendar.DATE));
            parametrosExecucao.put(nome, dataReferencia);
        } else {
            parametrosExecucao.put(nome, parametro);
        }
    }


    protected void getParametrosMetricas() throws Exception {
        BigDecimal numeroUnicoMetrica = getParametroBigDecimal("numeroUnicoMetrica");
        if (numeroUnicoMetrica != null) {
            Collection<DynamicVO> parametroMetricaVOS = JapeFactory.dao("MGSCT_Parametro_Metrica").find("NUCONTRMETRICA = ?", numeroUnicoMetrica);

            parametrosMetrica = new HashMap<>();

            for (DynamicVO parametroMetricaVO : parametroMetricaVOS) {
                String tipo = parametroMetricaVO.asString("MGSCT_Apoio_Parametro_Metrica.TIPO");
                String descricaoParametro = parametroMetricaVO.asString("MGSCT_Apoio_Parametro_Metrica.DESCRPARAM");
                String valor = parametroMetricaVO.asString("VALOR");
                Object valorConvertido = converteParametro(valor, tipo);
                parametrosMetrica.put(descricaoParametro, valorConvertido);
            }
        }
    }

    private Object converteParametro(String valor, String tipo) {
        switch (tipo) {
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

    protected BigDecimal getParametroBigDecimal(String nome) {
        return (BigDecimal) parametrosExecucao.get(nome);
    }

    protected Timestamp getParametroTimestamp(String nome) {
        return (Timestamp) parametrosExecucao.get(nome);
    }

    protected String getParametroString(String nome) {
        return (String) parametrosExecucao.get(nome);
    }
}
