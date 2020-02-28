package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface GeraFila {
    public boolean executar() throws Exception;
    public String getMensagem();
    public void setNumeroUnicoMetrica(BigDecimal numeroUnicoMetrica);
    public void setNumeroUnidadeFaturamento(BigDecimal numeroUnidadeFaturamento);
    public void setDataReferencia(Timestamp dataReferencia);
    public void setNomeProcessamento(String nomeProcessamento);
    public void setNumeroProcontrato(BigDecimal numeroContrato);
}
