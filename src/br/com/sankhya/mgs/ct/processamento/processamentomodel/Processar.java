package br.com.sankhya.mgs.ct.processamento.processamentomodel;

import java.math.BigDecimal;

public interface Processar {
    public boolean executar() throws Exception;
    public void finalizar();
    public String getMensagem();
    public void setNumeroUnicoFilaProcessamento(BigDecimal numeroUnicoFilaProcessamento);
}
