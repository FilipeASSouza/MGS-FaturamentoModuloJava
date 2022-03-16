package br.com.sankhya.mgs.ct.gerafilaprocessamento.gerafilamodel;

public interface GeraFila {
    public boolean executar() throws Exception;
    public String getMensagem();
    //public void setNumeroUnicoMetrica(BigDecimal numeroUnicoMetrica);
    //public void setNumeroUnidadeFaturamento(BigDecimal numeroUnidadeFaturamento);
    //public void setDataReferencia(Timestamp dataReferencia);
    //public void setNomeProcessamento(String nomeProcessamento);
    //public void setNumeroContrato(BigDecimal numeroContrato);
    public void setParametroExecucao(String nome, Object parametro);
}
