package br.com.sankhya.mgs.ct.agenda.processamentomodel;

import java.math.BigDecimal;

public interface Processar {
    public boolean executar() throws Exception;
    public String getMensagem();
    public void setNumeroUnicoFilaProcessamento(BigDecimal numeroUnicoFilaProcessamento);
}
